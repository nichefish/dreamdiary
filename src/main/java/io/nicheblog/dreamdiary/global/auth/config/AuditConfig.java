package io.nicheblog.dreamdiary.global.auth.config;

import io.nicheblog.dreamdiary.global.auth.handler.AuditAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * AuditConfig
 * <pre>
 *  JpaAudit 설정 (등록/수정자, 등록/수정일시 자동화)
 * </pre>
 *
 * @author nichefish
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorRef", modifyOnCreate = false)
public class AuditConfig {

    /**
     * 현재 작업자 감지
     */
    @Bean
    public AuditAwareImpl auditorRef() {
        return new AuditAwareImpl();
    }
}
