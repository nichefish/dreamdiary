package io.nicheblog.dreamdiary.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * TestConfig
 *
 * @author nichefish
 */
@Configuration
public class TestConfig {

    /**
     * 테스트용 auditor 세팅
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("test_user"); // 테스트 사용자 이름
    }
}