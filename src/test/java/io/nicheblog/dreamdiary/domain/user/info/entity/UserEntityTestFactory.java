package io.nicheblog.dreamdiary.domain.user.info.entity;

import io.nicheblog.dreamdiary.domain.user.emplym.entity.UserEmplymEntity;
import io.nicheblog.dreamdiary.domain.user.profl.entity.UserProflEntity;
import io.nicheblog.dreamdiary.global.TestConstant;
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
     * 테스트용 사용자 정보 Entity 객체 생성
     */
    public static UserEntity create() throws Exception {
        // 객체 생성
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

    /**
     * 테스트용 사용자 정보 Entity 객체 생성
     */
    public static UserEntity create(UserProflEntity profl) throws Exception {
        // 객체 생성
        UserEntity entity = create();
        entity.setProfl(profl);
        return entity;
    }

    /**
     * 테스트용 사용자 정보 Entity 객체 생성
     */
    public static UserEntity create(UserEmplymEntity emplym) throws Exception {
        // 객체 생성
        UserEntity entity = create();
        entity.setEmplym(emplym);
        return entity;
    }

    /**
     * 테스트용 사용자 정보 Entity 객체 생성
     */
    public static UserEntity create(UserProflEntity profl, UserEmplymEntity emplym) throws Exception {
        // 객체 생성
        UserEntity entity = create();
        entity.setProfl(profl);
        entity.setEmplym(emplym);
        return entity;
    }
}
