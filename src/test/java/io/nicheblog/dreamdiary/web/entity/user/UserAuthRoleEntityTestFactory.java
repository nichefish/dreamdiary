package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.auth.Auth;
import lombok.experimental.UtilityClass;

/**
 * UserAuthRoleEntityTestFactory
 * <pre>
 *  사용자 권한 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class UserAuthRoleEntityTestFactory {

    /**
     * 사용자 권한 Entity 생성
     */
    public static UserAuthRoleEntity getUserAuthRoleEntity(Auth auth) {
        return UserAuthRoleEntity.builder()
                .authCd(auth.name())
                .build();
    }
}
