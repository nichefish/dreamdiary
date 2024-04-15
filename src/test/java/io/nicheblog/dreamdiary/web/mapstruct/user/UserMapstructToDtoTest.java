package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.Auth;
import io.nicheblog.dreamdiary.web.entity.BaseEntityTestHelper;
import io.nicheblog.dreamdiary.web.entity.user.*;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntityTestFactory;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntityTestFactory;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMapstructToDtoTest
 * <pre>
 *  사용자 계정 신청 Mapstruct 매핑 테스트 모듈 :: toDto 분리
 * </pre>
 *
 * @author nichefish
 */
@Log4j2
class UserMapstructToDtoTest {

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;

    /**
     * toDto 검증 :: 기본 체크
     */
    @Test
    void testToDto_checkBasic() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();

        // When::
        UserDto.DTL dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        // 이메일 변환 로직 검증
        assertEquals(dto.getEmailId(), userEntity.getEmail().split("@")[0]);
        assertEquals(dto.getEmailDomain(), userEntity.getEmail().split("@")[1]);
    }

    /**
     * toDto 검증 :: 등록자/수정자 정보 체크
     */
    @Test
    void testToDto_checkAuditor() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();
        // 등록자
        BaseEntityTestHelper.setRegstrInfo(userEntity);
        // 수정자
        BaseEntityTestHelper.setMdfusrInfo(userEntity);

        // When::
        UserDto.DTL dto = userMapstruct.toDto(userEntity);

        // Then::
        assertNotNull(dto);
        // 등록자
        assertEquals(dto.getRegstrId(), "test_reg_user");
        assertEquals(dto.getRegstrNm(), "test_reg_nick_nm");
        assertEquals(dto.getRegDt(), "2000-01-01 00:00:00");
        // 수정자
        assertEquals(dto.getMdfusrId(), "test_mdf_user");
        assertEquals(dto.getMdfusrNm(), "test_mdf_nick_nm");
        assertEquals(dto.getMdfDt(), "2000-01-01 00:00:00");
    }

    /**
     * toDto 검증 :: 권한 체크
     */
    @Test
    void testToDto_checkAuth() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();
        // AUTH
        UserAuthRoleEntity aa = UserAuthRoleEntityTestFactory.getUserAuthRoleEntity(Auth.USER);
        UserAuthRoleEntity bb = UserAuthRoleEntityTestFactory.getUserAuthRoleEntity(Auth.MNGR);
        userEntity.setAuthList(List.of(aa, bb));
        // STUS
        userEntity.setAcntStus(new UserStusEmbed());

        // When::
        UserDto.DTL dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        // 권한 관련 매핑 검증
        assertFalse(CollectionUtils.isEmpty(dto.getAuthList()));
        assertEquals(dto.getAuthList().size(), 2);
        assertEquals(dto.getAuthList().get(0).getAuthCd(), Constant.AUTH_USER);
        assertEquals(dto.getAuthList().get(1).getAuthCd(), Constant.AUTH_MNGR);
    }

    /**
     * toDto 검증 :: 접속 IP 체크
     */
    @Test
    void testToDto_checkAcsIp() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();
        userEntity.setUseAcsIpYn("Y");
        userEntity.setAcsIpList(List.of());
        // AUTH
        UserAcsIpEntity aa = UserAcsIpEntityTestFactory.createUserAcsIpEntity("1,1,1,1");
        UserAcsIpEntity bb = UserAcsIpEntityTestFactory.createUserAcsIpEntity("2,2,2,2");
        userEntity.setAcsIpList(List.of(aa, bb));

        // When::
        UserDto.DTL dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        // 접속 IP 관련 매핑 검증
        assertNotNull(dto.getAcsIpList());
        assertNotNull(dto.getAcsIpList().get(0).getAcsIp(), "1.1.1.1");
        assertNotNull(dto.getAcsIpList().get(0).getAcsIp(), "2.2.2.2");
        assertEquals(dto.getAcsIpListStr(), "1.1.1.1,2.2.2.2");
    }

    /**
     * toDto 검증 :: 사용자 프로필 정보 체크
     */
    @Test
    void testToDto_checkProfl() throws Exception {
        // Given::
        UserProflEntity userProflEntity = UserProflEntityTestFactory.createUserProflEntity();
        UserEntity userEntity = UserEntityTestFactory.createUser(userProflEntity);

        // When::
        UserDto dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        UserProflDto userProflDto = dto.getProfl();
        assertNotNull(userProflDto);
        // 날짜 변환 체크
        assertEquals(userProflDto.getBrthdy(), "2000-01-01");
    }

    /**
     * toDto 검증 :: 사용자 인사정보 체크
     */
    @Test
    void testToDto_checkEmplym() throws Exception {
        // Given::
        UserEmplymEntity userEmplymEntity = UserEmplymEntityTestFactory.createUserEmplymEntity();
        UserEntity userEntity = UserEntityTestFactory.createUser(userEmplymEntity);

        // When::
        UserDto dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        UserEmplymDto userEmplymDto = dto.getEmplym();
        assertNotNull(userEmplymDto);
        // 날짜 변환 체크
        assertEquals(userEmplymDto.getEcnyDt(), "2000-01-01");
        assertEquals(userEmplymDto.getRetireDt(), "2000-01-01");
        // 이메일 변환 로직
        assertEquals(userEmplymDto.getEmplymEmailId(), userEmplymEntity.getEmplymEmail().split("@")[0]);
        assertEquals(userEmplymDto.getEmplymEmailDomain(), userEmplymEntity.getEmplymEmail().split("@")[1]);
    }

    /* ----- */

    /**
     * toDto 검증 :: 기본 체크
     */
    @Test
    void testToListDto_checkBasic() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();

        // When::
        UserDto.LIST dto = userMapstruct.toListDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        // 이메일 변환 로직 검증
        assertEquals(dto.getEmailId(), userEntity.getEmail().split("@")[0]);
        assertEquals(dto.getEmailDomain(), userEntity.getEmail().split("@")[1]);
    }

    /**
     * toListDto 검증 :: 등록자/수정자 정보 체크
     */
    @Test
    void testToListDto_checkAuditor() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();

        // When::
        UserDto.LIST dto = userMapstruct.toListDto(userEntity);

        // Then::
        assertNotNull(dto);
        // 등록자
        assertEquals(dto.getRegstrId(), "test_reg_user");
        assertEquals(dto.getRegstrNm(), "test_reg_nick_nm");
        assertEquals(dto.getRegDt(), "2000-01-01 00:00:00");
        // 수정자
        assertEquals(dto.getMdfusrId(), "test_mdf_user");
        assertEquals(dto.getMdfusrNm(), "test_mdf_nick_nm");
        assertEquals(dto.getMdfDt(), "2000-01-01 00:00:00");
    }

}