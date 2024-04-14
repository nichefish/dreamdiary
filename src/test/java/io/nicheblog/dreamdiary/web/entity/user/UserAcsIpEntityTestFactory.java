package io.nicheblog.dreamdiary.web.entity.user;

import lombok.experimental.UtilityClass;

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
     * 사용자 접속 IP Entity 생성
     */
    public static UserAcsIpEntity createUserAcsIpEntity(String acsIp) {
        return UserAcsIpEntity.builder()
                .acsIp(acsIp)
                .build();
    }
}
