package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.web.model.UserAuthRoleDtoTestFactory;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.experimental.UtilityClass;

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
public class UserDtoTestFactory {

    public static UserDto.DTL createUser() {
        // 갹체 생성
        return UserDto.DTL.builder()
                .userId("test_user")
                .password("test_password")
                .authList(UserAuthRoleDtoTestFactory.getUserAuthRoleDtoList())
                .nickNm("test_nick_nm")
                .emailId("test_email_id")
                .emailDomain("test_email_domain")
                .cttpc("010-0101-0101")
                .useAcsIpYn("Y")
                .acsIpListStr("[{\"value\":\"1.1.1.1\"},{\"value\":\"2.2.2.2\"}]")
                .cn("test_cn")
                .build();
    }
    public static UserDto.DTL createUser(UserProflDto profl) {
        // 갹체 생성
        UserDto.DTL dto = createUser();
        dto.setProfl(profl);
        return dto;
    }
    public static UserDto.DTL createUser(UserEmplymDto emplym) {
        // 갹체 생성
        UserDto.DTL dto = createUser();
        dto.setEmplym(emplym);
        return dto;
    }
    public static UserDto.DTL createUser(UserProflDto profl, UserEmplymDto emplym) {
        // 갹체 생성
        UserDto.DTL dto = createUser();
        dto.setProfl(profl);
        dto.setEmplym(emplym);
        return dto;
    }
}
