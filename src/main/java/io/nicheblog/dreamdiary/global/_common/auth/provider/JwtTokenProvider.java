package io.nicheblog.dreamdiary.global._common.auth.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.nicheblog.dreamdiary.global._common.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.global._common.auth.service.AuthService;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * DreamdiaryAuthenticationProvider
 * <pre>
 *  Spring Security :: 로그인 및 인증 처리.
 *  비밀번호 체크 + 접속IP + 비밀번호 변경기간 체크기능 추가하여 구현
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class JwtTokenProvider {

    private final AuthService authService;
    private final AuthenticationHelper authenticationHelper;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param roles 사용자 권한 목록
     * @return {@link String} -- 생성된 JWT 토큰 문자열
     */
    public String createToken(final String userId, final List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("roles", roles);
        Date now = DateUtils.getCurrDate();
        
        final long tokenValidMillisecond = 1000L * 60 * 60;     // 1시간
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    
    /**
     * 주어진 JWT 토큰에서 사용자 인증 정보를 가져옵니다.
     *
     * @param token JWT 토큰
     * @return {@link Authentication} -- 사용자 인증 정보
     * @throws Exception 인증 과정에서 발생할 수 있는 예외
     */
    public Authentication getAuthentication(String token) throws Exception {
        String username = this.getUsernameFromToken(token);
        final AuthInfo authInfo = authService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken generatedAuthToken = authenticationHelper.doAuth(authInfo);
        return new UsernamePasswordAuthenticationToken(authInfo, null, authInfo.getAuthorities());
    }

    /**
     * JWT 토큰에서 사용자 이름을 추출합니다.
     *
     * @param token JWT 토큰
     * @return {@link String} -- 사용자 이름
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 요청에서 JWT 토큰을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return {@link String} -- 추출된 JWT 토큰 문자열
     */
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    /**
     * 주어진 JWT 토큰의 유효성을 검증합니다.
     *
     * @param token JWT 토큰
     * @return {@link Boolean} -- 유효한 경우 true, 그렇지 않은 경우 false
     */
    public Boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
