package io.nicheblog.dreamdiary.global.config;

import io.nicheblog.dreamdiary.global.TestConstant;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * TestAuditConfig
 * <pre>
 *  테스트용 auditor 정보 세팅 설정
 * </pre>
 *
 * @author nichefish
 */
@TestConfiguration
@Profile("test")
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class TestAuditConfig {

    /**
     * 테스트용 auditor 세팅
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(TestConstant.TEST_AUDITOR); // 테스트 사용자 이름
    }
}