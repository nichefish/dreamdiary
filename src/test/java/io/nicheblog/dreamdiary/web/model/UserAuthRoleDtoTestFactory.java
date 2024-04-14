package io.nicheblog.dreamdiary.web.model;

import io.nicheblog.dreamdiary.global.auth.Auth;
import io.nicheblog.dreamdiary.web.model.user.UserAuthRoleDto;
import lombok.experimental.UtilityClass;

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

    /**
     * 사용자 권한 Dto 생성
     */
    public static UserAuthRoleDto getUserAuthRoleDto(Auth auth) {
        return UserAuthRoleDto.builder()
                .authCd(auth.name())
                .build();
    }
}
