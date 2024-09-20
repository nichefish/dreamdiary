package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.Auth;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.UserAuthRoleDtoTestFactory;
import io.nicheblog.dreamdiary.web.model.user.UserAuthRoleDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.UserDtoTestFactory;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDtoTestFactory;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDtoTestFactory;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMapstructToEntityTest
 * <pre>
 *  사용자 계정 신청 Mapstruct 매핑 테스트 모듈 :: toEntity 분리
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
@Log4j2
class UserMapstructToEntityTest {

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;

    /**
     * dto -> entity 검증
     */
    @Test
    void testTestToEntity_checkBasic() throws Exception {
        // Given::
        UserDto.DTL userDto = UserDtoTestFactory.createUserDtlDto();

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        // 이메일 변환 로직
        assertEquals(entity.getEmail(), userDto.getEmailId() + "@" + userDto.getEmailDomain());
    }

    /**
     * dto -> entity 검증:: 권한
     */
    @Test
    void testTestToEntity_checkAuth() throws Exception {
        // Given::
        UserDto.DTL userDto = UserDtoTestFactory.createUserDtlDto();
        // AUTH
        UserAuthRoleDto aa = UserAuthRoleDtoTestFactory.getUserAuthRoleDto(Auth.USER);
        UserAuthRoleDto bb = UserAuthRoleDtoTestFactory.getUserAuthRoleDto(Auth.MNGR);
        userDto.setAuthList(List.of(aa, bb));

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        // 권한 관련 매핑 검증
        assertFalse(CollectionUtils.isEmpty(entity.getAuthList()));
        assertEquals(entity.getAuthList().size(), 2);
        assertEquals(entity.getAuthList().get(0).getAuthCd(), Constant.AUTH_USER);
        assertEquals(entity.getAuthList().get(1).getAuthCd(), Constant.AUTH_MNGR);
    }

    /**
     * dto -> entity 검증:: 접속 IP
     */
    @Test
    void testTestToEntity_checkAcsIp() throws Exception {
        // Given::
        UserDto.DTL userDto = UserDtoTestFactory.createUserDtlDto();
        // ACS_IP
        userDto.setUseAcsIpYn("Y");
        userDto.setAcsIpListStr("[{\"value\":\"1.1.1.1\"},{\"value\":\"2.2.2.2\"}]");

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        // 접속 IP 관련
        assertEquals(entity.getUseAcsIpYn(), userDto.getUseAcsIpYn());
        assertEquals(entity.getAcsIpList().get(0).getAcsIp(), "1.1.1.1");
        assertEquals(entity.getAcsIpList().get(1).getAcsIp(), "2.2.2.2");
    }

    /**
     * dto -> entity 검증:: 사용자 프로필 정보
     */
    @Test
    void testTestToEntity_checkProfl() throws Exception {
        // Given::
        UserProflDto userProflDto = UserProflDtoTestFactory.createUserProfl();
        UserDto.DTL userDto = UserDtoTestFactory.createUserDtlDto(userProflDto);

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        UserProflEntity userProflEntity = entity.getProfl();
        assertNotNull(userProflEntity);
        // 날짜 변환 체크
        assertEquals(userProflEntity.getBrthdy(), DateUtils.asDate("2000-01-01"));
    }

    /**
     * dto -> entity 검증:: 사용자 인사정보
     */
    @Test
    void testTestToEntity_checkEmplym() throws Exception {
        // Given::
        UserEmplymDto userEmplymDto = UserEmplymDtoTestFactory.createUserEmplym();
        UserDto.DTL userDto = UserDtoTestFactory.createUserDtlDto(userEmplymDto);

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        UserEmplymEntity userEmplymEntity = entity.getEmplym();
        assertNotNull(userEmplymEntity);
        // 날짜 변환 체크
        assertEquals(userEmplymEntity.getEcnyDt(), DateUtils.asDate("2000-01-01"));
        assertEquals(userEmplymEntity.getRetireDt(), DateUtils.asDate("2000-01-01"));
        // 이메일 변환 로직
        assertEquals(userEmplymEntity.getEmplymEmail(), userEmplymDto.getEmplymEmailId() + "@" + userEmplymDto.getEmplymEmailDomain());
    }

}