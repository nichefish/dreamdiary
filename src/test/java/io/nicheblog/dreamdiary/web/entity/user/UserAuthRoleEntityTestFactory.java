package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.Constant;
import lombok.experimental.UtilityClass;

import java.util.List;

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
     * 사용자 권한 Entity 목록 생성
     */
    public static List<UserAuthRoleEntity> getUserAuthRoleEntityList() {
        UserAuthRoleEntity aa = UserAuthRoleEntity.builder()
                .authCd(Constant.AUTH_USER)
                .build();
        UserAuthRoleEntity bb = UserAuthRoleEntity.builder()
                .authCd(Constant.AUTH_MNGR)
                .build();
        return List.of(aa, bb);
    }
}
