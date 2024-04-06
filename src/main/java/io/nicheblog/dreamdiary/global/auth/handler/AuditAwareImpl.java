package io.nicheblog.dreamdiary.global.auth.handler;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * AuditAwareImpl
 * <pre>
 *  현재 작업자(Auditor) 정보 조회 방법 세팅
 * </pre>
 *
 * @author nichefish
 */
public class AuditAwareImpl
        implements AuditorAware<String> {

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return Optional.empty();
        // 비로그인시 시스템 계정(system)으로 자동 처리
        Object principal = authentication.getPrincipal();
        boolean isNotLgn = (principal instanceof String);
        if (isNotLgn) return Optional.of(Constant.SYSTEM_ACNT);
        // 로그인 사용자 ID 반환
        AuthInfo authInfo = (AuthInfo) authentication.getPrincipal();
        return Optional.of(authInfo.getUserId());
    }
}
