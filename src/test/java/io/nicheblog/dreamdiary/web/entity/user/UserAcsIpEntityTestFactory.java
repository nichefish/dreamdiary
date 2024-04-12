package io.nicheblog.dreamdiary.web.entity.user;

import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * UserAcsIpEntityTestFactory
 * <pre>
 *  사용자 접속 IP 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class UserAcsIpEntityTestFactory {

    /**
     * 사용자 접속 IP Entity List 생성
     */
    public static List<UserAcsIpEntity> createUserAcsIpEntityList() {
        UserAcsIpEntity aa = UserAcsIpEntity.builder()
                .acsIp("1.1.1.1")
                .build();
        UserAcsIpEntity bb = UserAcsIpEntity.builder()
                .acsIp("2.2.2.2")
                .build();
        return List.of(aa, bb);
    }

    /**
     * 사용자 접속 IP 문자열 생성
     */
    public static List<String> createUserAcsIpStrList() {
        return List.of("1.1.1.1", "2.2.2.2");
    }
}
