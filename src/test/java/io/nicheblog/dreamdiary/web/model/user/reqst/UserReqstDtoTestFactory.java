package io.nicheblog.dreamdiary.web.model.user.reqst;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.model.user.UserAuthRoleDto;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * UserReqstDtoTestFactory
 * <pre>
 *  사용자 계정 신청 테스트 Dto 생성 팩토리 모듈
 * </pre>
 * TODO: 케이스별로 생성 로직 세분화
 * 
 * @author nichefish 
 */
@UtilityClass
public class UserReqstDtoTestFactory {

    /**
     * 사용자 신청 정보 Dto 객체 생성
     */
    public static UserReqstDto createUserReqst() {
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
                .build();
    }
    public static UserReqstDto createUserReqst(UserProflDto profl) {
        // 갹체 생성
        UserReqstDto dto = createUserReqst();
        dto.setProfl(profl);
        return dto;
    }
    public static UserReqstDto createUserReqst(UserEmplymDto emplym) {
        // 갹체 생성
        UserReqstDto dto = createUserReqst();
        dto.setEmplym(emplym);
        return dto;
    }
    public static UserReqstDto createUserReqst(UserProflDto profl, UserEmplymDto emplym) {
        // 갹체 생성
        UserReqstDto dto = createUserReqst();
        dto.setProfl(profl);
        dto.setEmplym(emplym);
        return dto;
    }
}
