package io.nicheblog.dreamdiary.web.model.user.reqst;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.model.user.UserAuthRoleDto;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

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
@ActiveProfiles("test")
public class UserReqstDtoTestFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 사용자 신청 정보 Dto 객체 생성
     */
    public static UserReqstDto createUserReqst() {
        // 갹체 생성
        return UserReqstDto.builder()
                .userId("test_user")
                .password("test_password")
                // 권한:: 운영 로직에선 변환 전 서비스단에서 할당하여 넣어줌.
                .authList(List.of(UserAuthRoleDto.builder().authCd(Constant.AUTH_USER).build()))
                .nickNm("test_nick_nm")
                .emailId("test_email_id")
                .emailDomain("test_email_domain")
                .cttpc("010-0101-0101")
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

    /**
     * 사용자 계정 신청 JSON 문자열 생성
     */
    public static String createUserReqstJson() throws JsonProcessingException {
        UserReqstDto userReqstDto = createUserReqst();
        return objectMapper.writeValueAsString(userReqstDto);
    }
}
