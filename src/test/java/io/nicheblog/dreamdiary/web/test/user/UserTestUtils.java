package io.nicheblog.dreamdiary.web.test.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserAuthRoleEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.user.UserAuthRoleDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * UserTestUtils
 * <pre>
 *  사용자 관련 테스트 공통기능 모아놓은 유틸리티 클래스
 * </pre>
 * 
 * @author nichefish 
 */
@UtilityClass
public class UserTestUtils {

    /**
     * 사용자 신청 - 프로필 정보 Dto 객체 생성 :: 메소드 분리
     */
    public static UserProflDto createUserProfl() {
        // 갹체 생성
        return UserProflDto.builder()
                .brthdy("2000-01-01")
                .proflCn("test_profl_cn")
                .build();
    }

    /**
     * 사용자 신청 - 인사정보 Dto 객체 생성 :: 메소드 분리
     */
    public static UserEmplymDto createUserEmplym() {
        // 갹체 생성
        return UserEmplymDto.builder()
                .userNm("1987-03-11")
                .cmpyCd("test_cmpy_cd")
                .teamCd("test_team_cd")
                .emplymCd("test_emplym_cd")
                .rankCd("test_rank")
                .apntcYn("Y")
                .emplymEmailId("test_emplym_email_id")
                .emplymEmailDomain("test_emplym_email_domain")
                .emplymCttpc("000-0000-0000")
                .ecnyDt("2000-01-01")
                .retireYn("Y")
                .retireDt("2000-01-01")
                .acntBank("test_acnt_bank")
                .acntNo("test-acnt_no")
                .emplymCn("test_emplym_cn")
                .build();
    }

    /**
     * 사용자 정보 Entity 객체 생성
     */
    public static UserEntity createUserEntity(UserProflEntity profl, UserEmplymEntity emplym) {
        // 갹체 생성
        return UserEntity.builder()
                .userId("test_user")
                .password("test_password_encoded")
                .authList(UserTestUtils.getUserAuthRoleEntityList())
                .nickNm("test_nick_nm")
                .email("test_email_id@test_email_domain")
                .cttpc("010-0101-0101")
                .useAcsIpYn("Y")
                .acsIpList(UserTestUtils.createUserAcsIpEntityList())
                .acsIpStrList(UserTestUtils.createUserAcsIpStrList())
                .cn("test_cn")
                .profl(profl)
                .emplym(emplym)
                .acntStus(new UserStusEmbed())
                .build();
    }

    /**
     * 사용자 신청 - 프로필 정보 Entity 객체 생성 :: 메소드 분리
     */
    public static UserProflEntity createUserProflEntity() throws Exception {
        // 갹체 생성
        return UserProflEntity.builder()
                .brthdy(DateUtils.asDate("2000-01-01"))
                .proflCn("test_profl_cn")
                .build();
    }

    /**
     * 사용자 신청 - 인사정보 Dto 객체 생성 :: 메소드 분리
     */
    public static UserEmplymEntity createUserEmplymEntity() throws Exception {
        // 갹체 생성
        return UserEmplymEntity.builder()
                .userNm("test_user")
                .cmpyCd("test_cmpy_cd")
                .teamCd("test_team_cd")
                .emplymCd("test_emplym_cd")
                .rankCd("test_rank")
                .apntcYn("Y")
                .emplymEmail("test_emplym_email_id@test_emplym_email_domain")
                .emplymCttpc("000-0000-0000")
                .ecnyDt(DateUtils.asDate("2000-01-01"))
                .retireYn("Y")
                .retireDt(DateUtils.asDate("2000-01-01"))
                .acntBank("test_acnt_bank")
                .acntNo("test-acnt_no")
                .emplymCn("test_emplym_cn")
                .build();
    }

    public static List<UserAcsIpEntity> createUserAcsIpEntityList() {
        UserAcsIpEntity aa = UserAcsIpEntity.builder()
                .acsIp("1.1.1.1")
                .build();
        UserAcsIpEntity bb = UserAcsIpEntity.builder()
                .acsIp("2.2.2.2")
                .build();
        return List.of(aa, bb);
    }

    public static List<String> createUserAcsIpStrList() {
        return List.of("1.1.1.1", "2.2.2.2");
    }

    public static UserDto.DTL createUser(UserProflDto profl, UserEmplymDto emplym) {
        // 갹체 생성
        return UserDto.DTL.builder()
                .userId("test_user")
                .password("test_password")
                .authList(UserTestUtils.getUserAuthRoleDtoList())
                .nickNm("test_nick_nm")
                .emailId("test_email_id")
                .emailDomain("test_email_domain")
                .cttpc("010-0101-0101")
                .useAcsIpYn("Y")
                .acsIpListStr("[{\"value\":\"1.1.1.1\"},{\"value\":\"2.2.2.2\"}]")
                .cn("test_cn")
                .profl(profl)
                .emplym(emplym)
                .build();
    }

    public static List<UserAuthRoleEntity> getUserAuthRoleEntityList() {
        UserAuthRoleEntity aa = UserAuthRoleEntity.builder()
                .authCd(Constant.AUTH_USER)
                .build();
        UserAuthRoleEntity bb = UserAuthRoleEntity.builder()
                .authCd(Constant.AUTH_MNGR)
                .build();
        return List.of(aa, bb);
    }

    public static List<UserAuthRoleDto> getUserAuthRoleDtoList() {
        UserAuthRoleDto aa = UserAuthRoleDto.builder()
                .authCd(Constant.AUTH_USER)
                .build();
        UserAuthRoleDto bb = UserAuthRoleDto.builder()
                .authCd(Constant.AUTH_MNGR)
                .build();
        return List.of(aa, bb);
    }
}
