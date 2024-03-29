package io.nicheblog.dreamdiary.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ActiveProfile
 * <pre>
 *  현재 활성 중인 프로필 정보를 가져온다. (local / dev / stg / prod / test)
 * </pre>
 *
 * @author nichefish
 */
@Component("activeProfile")
@ConfigurationProperties(prefix = "spring.profiles")
@Getter
@Setter
public final class ActiveProfile {

    /**
     * 프로필 Enum
     */
    @RequiredArgsConstructor
    public enum Profile {
        LOCAL("local", "로컬 환경"),
        DEV("dev", "개발 환경"),
        STG("stg", "스테이징 환경"),
        PROD("prod", "운영 환경"),
        TEST("test", "테스트 환경");

        public final String key;
        public final String desc;
    }

    private Profile profile;

    private final String PROFILE_LOCAL = Profile.LOCAL.key;
    private final String PROFILE_DEV = Profile.DEV.key;
    private final String PROFILE_STG = Profile.STG.key;
    private final String PROFILE_PROD = Profile.PROD.key;
    private final String PROFILE_TEST = Profile.TEST.key;

    /**
     * 활성화된 프로필 항목
     */
    private String active;

    /* ----- */

    public Boolean isLocal() {
        return PROFILE_LOCAL.equals(this.active);
    }

    public Boolean isDev() {
        return PROFILE_DEV.equals(this.active);
    }

    public Boolean isStg() {
        return PROFILE_STG.equals(this.active);
    }

    public Boolean isProd() {
        return PROFILE_PROD.equals(this.active);
    }

    public Boolean isTest() {
        return PROFILE_TEST.equals(this.active);
    }
}
