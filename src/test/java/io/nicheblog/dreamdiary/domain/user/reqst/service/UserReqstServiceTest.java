package io.nicheblog.dreamdiary.domain.user.reqst.service;

import io.nicheblog.dreamdiary.domain.user.emplym.model.UserEmplymDtoTestFactory;
import io.nicheblog.dreamdiary.domain.user.info.entity.UserEntity;
import io.nicheblog.dreamdiary.domain.user.info.model.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.domain.user.info.model.profl.UserProflDto;
import io.nicheblog.dreamdiary.domain.user.info.repository.jpa.UserRepository;
import io.nicheblog.dreamdiary.domain.user.profl.model.UserProflDtoTestFactory;
import io.nicheblog.dreamdiary.domain.user.reqst.mapstruct.UserReqstMapstruct;
import io.nicheblog.dreamdiary.domain.user.reqst.model.UserReqstDto;
import io.nicheblog.dreamdiary.domain.user.reqst.model.UserReqstDtoTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UserReqstServiceTest
 * <pre>
 *  사용자 신규계정 신청 프로세스 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
class UserReqstServiceTest {

    private final UserReqstMapstruct userReqstMapstruct = UserReqstMapstruct.INSTANCE;

    @Resource(name = "userReqstService")
    private UserReqstService userReqstService;
    @MockBean(name = "userRepository")
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void regist() throws Exception {
        // Given::
        UserProflDto profl = UserProflDtoTestFactory.create();
        UserEmplymDto emplym = UserEmplymDtoTestFactory.create();
        UserReqstDto userReqstDto = UserReqstDtoTestFactory.create(profl, emplym);

        // userReqstService.regist() 내에서 호출될 MockBean들에 대해 행동 방침을 정해준다.
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        // 실제 Mapstruct 객체 사용 (사전에 mapstruct test가 먼저 끝나야 함. 여기서는 정상적으로 작동한다고 가정
        UserEntity userEntity = userReqstMapstruct.toEntity(userReqstDto);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userReqstMapstruct.toDto(any(UserEntity.class))).thenReturn(userReqstDto);

        // When::
        UserReqstDto resultDto = userReqstService.regist(userReqstDto);

        // Then::
        assertNotNull(resultDto);
        verify(userRepository).save(any(UserEntity.class));
        // passwordEncoder 정상 실행여부 체크
        assertEquals("encoded_password", userEntity.getPassword());
    }
}