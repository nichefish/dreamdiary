package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import lombok.experimental.UtilityClass;

/**
 * UserEntityTestFactory
 * <pre>
 *  사용자 계정 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class UserEntityTestFactory {

    /**
     * 사용자 정보 Entity 객체 생성
     */
    public static UserEntity createUserEntity(UserProflEntity profl, UserEmplymEntity emplym) throws Exception {
        // 갹체 생성
        return UserEntity.builder()
                .userId("test_user")
                .password("test_password_encoded")
                .authList(UserAuthRoleEntityTestFactory.getUserAuthRoleEntityList())
                .nickNm("test_nick_nm")
                .email("test_email_id@test_email_domain")
                .cttpc("010-0101-0101")
                .useAcsIpYn("Y")
                .acsIpList(UserAcsIpEntityTestFactory.createUserAcsIpEntityList())
                .acsIpStrList(UserAcsIpEntityTestFactory.createUserAcsIpStrList())
                .cn("test_cn")
                .profl(profl)
                .emplym(emplym)
                .acntStus(new UserStusEmbed())
                .regstrId("test_reg_user")
                .regstrInfo(AuditorInfo.builder().userId("test_reg_user").nickNm("test_reg_nick_nm").build())
                .regDt(DateUtils.asDate("2000-01-01"))
                .mdfusrId("test_mdf_user")
                .mdfusrInfo(AuditorInfo.builder().userId("test_mdf_user").nickNm("test_reg_nick_nm").build())
                .mdfDt(DateUtils.asDate("2000-01-01"))
                .build();
    }
}
