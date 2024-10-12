package io.nicheblog.dreamdiary.domain.user.info.model;

import io.nicheblog.dreamdiary.domain.user.info.model.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.domain.user.info.model.profl.UserProflDto;
import io.nicheblog.dreamdiary.global.TestConstant;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * UserDtoTestFactory
 * <pre>
 *  사용자 계정 테스트 Dto 생성 팩토리 모듈
 * </pre>
 * TODO: 케이스별로 생성 로직 세분화
 * 
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class UserDtoTestFactory {

    /**
     * 테스트용 사용자 Dto (상세) 생성
     */
    public static UserDto.DTL createUserDtlDto() {
        // 갹체 생성
        return UserDto.DTL.builder()
                .userId(TestConstant.TEST_USER)
                .password("test_password")
                .nickNm("test_nick_nm")
                .emailId("test_email_id")
                .emailDomain("test_email_domain")
                .cttpc("010-0101-0101")
                .cn("test_cn")
                .build();
    }

    /**
     * 테스트용 사용자 Dto (상세) 생성
     */
    public static UserDto.DTL createUserDtlDto(UserProflDto profl) {
        // 갹체 생성
        UserDto.DTL dto = createUserDtlDto();
        dto.setProfl(profl);
        return dto;
    }

    /**
     * 테스트용 사용자 Dto (상세) 생성
     */
    public static UserDto.DTL createUserDtlDto(UserEmplymDto emplym) {
        // 갹체 생성
        UserDto.DTL dto = createUserDtlDto();
        dto.setEmplym(emplym);
        return dto;
    }

    /**
     * 테스트용 사용자 Dto (상세) 생성
     */
    public static UserDto.DTL createUserDtlDto(UserProflDto profl, UserEmplymDto emplym) {
        // 갹체 생성
        UserDto.DTL dto = createUserDtlDto();
        dto.setProfl(profl);
        dto.setEmplym(emplym);
        return dto;
    }
}
