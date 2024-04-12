package io.nicheblog.dreamdiary.web.model;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.model.user.UserAuthRoleDto;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * UserAuthRoleDtoTestFactory
 * <pre>
 *  사용자 권한 테스트 Dto 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish
 */
@UtilityClass
public class UserAuthRoleDtoTestFactory {
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
