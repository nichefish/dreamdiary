package io.nicheblog.dreamdiary.auth.security.provider;

import io.nicheblog.dreamdiary.auth.jwt.provider.JwtTokenProvider;
import io.nicheblog.dreamdiary.auth.security.exception.AuthenticationFailureException;
import io.nicheblog.dreamdiary.auth.security.model.AuthInfo;
import io.nicheblog.dreamdiary.auth.security.provider.helper.AuthenticationHelper;
import io.nicheblog.dreamdiary.auth.security.service.AuthService;
import io.nicheblog.dreamdiary.global.util.CookieUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DreamdiaryAuthenticationProvider
 * <pre>
 *  Spring Security :: 로그인 및 인증 처리.
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class DreamdiaryAuthenticationProvider
        implements AuthenticationProvider {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationHelper authenticationHelper;

    /**
     * Spring Security :: 사용자 인증 과정
     * 인증 성공시 응딥으로 JWT 토큰을 반환한다.
     *
     * @param authentication 인증 정보를 담고 있는 Authentication 객체
     * @return {@link Authentication} -- 인증된 사용자의 Authentication 객체
     * @throws AuthenticationException 처리 중 발생할 수 있는 예외
     */
    @SneakyThrows
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final AuthInfo authInfo = authService.loadUserByUsername(username);

        // 인증 객체 생성
        final Boolean isValidated = authenticationHelper.validateAuth(authentication, authInfo);
        if (!isValidated) throw new AuthenticationFailureException(MessageUtils.getExceptionMsg("AuthenticationFailureException"));
        final UsernamePasswordAuthenticationToken generatedAuthToken = authInfo.getAuthToken();

        // 인증 객체를 기반으로 JWT 생성, 임시로 세션에 저장
        final String jwt = this.authenticateAndGenerateJwt(generatedAuthToken);
        // 세션에 JWT 저장
        final ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpSession session = servletRequestAttribute.getRequest().getSession();
        session.setAttribute("jwt", jwt);
        // HTTP 쿠키에 JWT 저장
        CookieUtils.setJwtCookie(jwt); // 7일간 유지
        // HttpServletResponse 응답 헤더에 JWT 세팅
        final HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        if (response != null) response.setHeader("Authorization", "Bearer " + jwt);

        return generatedAuthToken;
    }

    /**
     * 인증 결과인 {@link Authentication} 객체를 사용하여 JWT 토큰을 생성합니다.
     *
     * @param authentication 인증 정보를 담고 있는 {@link Authentication} 객체.
     * @return {@link String} -- 생성된 JWT 토큰 문자열. 클라이언트에서 인증을 위한 목적으로 사용됩니다.
     */
    public String authenticateAndGenerateJwt(final Authentication authentication) {
        final String username = authentication.getName();
        final List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return jwtTokenProvider.createToken(username, roles); // JWT 생성
    }

    /**
     * 인증 제공자가 특정 인증 클래스 타입을 지원하는지 여부를 확인합니다.
     * -> UsernamePasswordAuthenticationToken만 처리합니다.
     *
     * @param authentication 검사할 인증 클래스 타입
     * @return {@link Boolean} -- 인증 제공자가 해당 인증 클래스 타입을 지원하는 경우 true
     */
    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
