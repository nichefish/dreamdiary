package io.nicheblog.dreamdiary.web.entity.user.profl;

import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.experimental.UtilityClass;

/**
 * UserProflEntityTestFactory
 * <pre>
 *  사용자 프로필 정보 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class UserProflEntityTestFactory {

    /**
     * 사용자 프로필 정보 Entity 객체 생성
     */
    public static UserProflEntity createUserProflEntity() throws Exception {
        // 갹체 생성
        return UserProflEntity.builder()
                .brthdy(DateUtils.asDate("2000-01-01"))
                .proflCn("test_profl_cn")
                .build();
    }
}
