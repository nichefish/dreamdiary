package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.entity.user.UserAuthRoleEntity;
import io.nicheblog.dreamdiary.web.model.UserAuthRoleDtoTestFactory;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * UserDtoTestFactory
 * <pre>
 *  사용자 계정 테스트 Dto 생성 팩토리 모듈
 * </pre>
 * 
 * @author nichefish 
 */
@UtilityClass
public class UserDtoTestFactory {

    public static UserDto.DTL createUser(UserProflDto profl, UserEmplymDto emplym) {
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
                .profl(profl)
                .emplym(emplym)
                .build();
    }
}
