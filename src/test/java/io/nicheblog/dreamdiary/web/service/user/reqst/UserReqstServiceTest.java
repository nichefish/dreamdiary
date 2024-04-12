package io.nicheblog.dreamdiary.web.service.user.reqst;

import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.mapstruct.user.reqst.UserReqstMapstruct;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDtoTestFactory;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDtoTestFactory;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDtoTestFactory;
import io.nicheblog.dreamdiary.web.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

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
@SpringBootTest
class UserReqstServiceTest {

    private final UserReqstMapstruct userReqstMapstruct = UserReqstMapstruct.INSTANCE;

    @Resource(name = "userReqstService")
    private UserReqstService userReqstService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void regist() throws Exception {
        // Given::
        UserProflDto profl = UserProflDtoTestFactory.createUserProfl();
        UserEmplymDto emplym = UserEmplymDtoTestFactory.createUserEmplym();
        UserReqstDto userReqstDto = UserReqstDtoTestFactory.createUserReqst(profl, emplym);

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

    /* ----- */

}