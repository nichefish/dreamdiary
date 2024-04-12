package io.nicheblog.dreamdiary.web.test.user.reqst;

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
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
import io.nicheblog.dreamdiary.web.test.user.UserTestUtils;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * UserReqstTestUtils
 * <pre>
 *  사용자 관련 테스트 공통기능 모아놓은 유틸리티 클래스
 * </pre>
 * 
 * @author nichefish 
 */
@UtilityClass
public class UserReqstTestUtils {

    /**
     * 사용자 신청 정보 Dto 객체 생성
     */
    public static UserReqstDto createUserReqst(UserProflDto profl, UserEmplymDto emplym) {
        // 갹체 생성
        return UserReqstDto.builder()
                .userId("test_user")
                .password("test_password")
                .authList(List.of(UserAuthRoleDto.builder().authCd(Constant.AUTH_USER).build()))
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
     * 사용자 정보 Entity 객체 생성
     */
    public static UserEntity createUserReqstEntity(UserProflEntity profl, UserEmplymEntity emplym) {
        // 갹체 생성
        return UserEntity.builder()
                .userId("test_user")
                .password("test_password_encoded")
                .authList(List.of(UserAuthRoleEntity.builder().authCd(Constant.AUTH_USER).build()))
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
}
