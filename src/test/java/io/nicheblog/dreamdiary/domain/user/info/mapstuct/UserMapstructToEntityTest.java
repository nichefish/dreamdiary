package io.nicheblog.dreamdiary.domain.user.info.mapstuct;

import io.nicheblog.dreamdiary.global._common.auth.Auth;
import io.nicheblog.dreamdiary.domain.user.emplym.entity.UserEmplymEntity;
import io.nicheblog.dreamdiary.domain.user.emplym.model.UserEmplymDtoTestFactory;
import io.nicheblog.dreamdiary.domain.user.info.entity.UserEntity;
import io.nicheblog.dreamdiary.domain.user.info.mapstruct.UserMapstruct;
import io.nicheblog.dreamdiary.domain.user.info.model.UserAuthRoleDto;
import io.nicheblog.dreamdiary.domain.user.info.model.UserAuthRoleDtoTestFactory;
import io.nicheblog.dreamdiary.domain.user.info.model.UserDto;
import io.nicheblog.dreamdiary.domain.user.info.model.UserDtoTestFactory;
import io.nicheblog.dreamdiary.domain.user.info.model.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.domain.user.info.model.profl.UserProflDto;
import io.nicheblog.dreamdiary.domain.user.profl.entity.UserProflEntity;
import io.nicheblog.dreamdiary.domain.user.profl.model.UserProflDtoTestFactory;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
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
 *  TODO: updateFromDto
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
@Log4j2
class UserMapstructToEntityTest {

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;

    /**
     * dto -> entity 변환 검증
     */
    @Test
    void testToEntity_checkBasic() throws Exception {
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
     * dto -> entity 변환 검증:: 권한
     */
    @Test
    void testToEntity_checkAuth() throws Exception {
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
     * dto -> entity 변환 검증:: 접속 IP
     */
    @Test
    void testToEntity_checkAcsIp() throws Exception {
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
     * dto -> entity 변환 검증:: 사용자 프로필 정보
     */
    @Test
    void testToEntity_checkProfl() throws Exception {
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
     * dto -> entity 변환 검증:: 사용자 인사정보
     */
    @Test
    void testToEntity_checkEmplym() throws Exception {
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

    /* ----- */

    /**
     * updateFromDto 검증 :: 기본 속성
     * TODO
     */
    @Test
    void testUpdateFromDto_checkBasic() throws Exception {
        //
    }
}