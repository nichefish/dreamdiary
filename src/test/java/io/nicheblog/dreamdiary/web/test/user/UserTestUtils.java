package io.nicheblog.dreamdiary.web.test.user;

import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
import lombok.experimental.UtilityClass;

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
     * 사용자 신청 정보 Dto 객체 생성
     */
    public static UserReqstDto createUserReqst(UserProflDto profl, UserEmplymDto emplym) {
        // 갹체 생성
        return UserReqstDto.builder()
                .userId("test_user")
                .password("test_password")
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
     * 사용자 신청 - 인사정보 Dto 객체 생성 :: 메소드 분리
     */
    public static UserEntity createUserEntity(Object o, Object o1) {
        return UserEntity.builder()
                .userId("test_user")
                .build();
    }
}
