package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * UserEntityTestFactory
 * <pre>
 *  사용자 계정 테스트 Entity 생성 팩토리 모듈
 * </pre>
 * TODO: 케이스별로 생성 로직 세분화
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class UserEntityTestFactory {

    /**
     * 사용자 정보 Entity 객체 생성
     */
    public static UserEntity createUser() throws Exception {
        // 갹체 생성
        return UserEntity.builder()
                .userId(TestConstant.TEST_USER)
                .password(TestConstant.TEST_PASSWORD_ENCODED)
                .nickNm(TestConstant.TEST_NICK_NM)
                .email("test_email_id@test_email_domain")
                .cttpc("010-0101-0101")
                .cn("test_cn")
                .acntStus(UserStusEmbed.builder().build())
                .build();
    }
    public static UserEntity createUser(UserProflEntity profl) throws Exception {
        // 갹체 생성
        UserEntity entity = createUser();
        entity.setProfl(profl);
        return entity;
    }
    public static UserEntity createUser(UserEmplymEntity emplym) throws Exception {
        // 갹체 생성
        UserEntity entity = createUser();
        entity.setEmplym(emplym);
        return entity;
    }
    public static UserEntity createUser(UserProflEntity profl, UserEmplymEntity emplym) throws Exception {
        // 갹체 생성
        UserEntity entity = createUser();
        entity.setProfl(profl);
        entity.setEmplym(emplym);
        return entity;
    }
}
