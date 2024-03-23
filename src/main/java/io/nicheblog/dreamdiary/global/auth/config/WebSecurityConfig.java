package io.nicheblog.dreamdiary.global.auth.config;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.handler.DreamdiaryAuthenticationProvider;
import io.nicheblog.dreamdiary.global.auth.handler.LgnFailureHandler;
import io.nicheblog.dreamdiary.global.auth.handler.LgnSuccessHandler;
import io.nicheblog.dreamdiary.global.auth.handler.LgoutHandler;
import io.nicheblog.dreamdiary.global.auth.service.AuthService;
import io.nicheblog.dreamdiary.web.SiteUrl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;

/**
 * WebSecurityConfig
 * <pre>
 *  Spring Security 설정
 * </pre>
 * TODO: deprecated된것 제거하기
 *
 * @author nichefish
 */
@Configuration
@Log4j2
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig
        extends WebSecurityConfigurerAdapter {

    @Value("${remember-me.key")
    private String REMEMBER_ME_KEY;
    @Value("${remember-me.param")
    private String REMEMBER_ME_PARAM;

    @Resource(name = "authService")
    private AuthService authService;

    @Resource(name = "lgnFailureHandler")
    private LgnFailureHandler lgnFailureHandler;

    @Resource(name = "lgnSuccessHandler")
    private LgnSuccessHandler lgnSuccessHandler;

    @Resource(name = "lgoutHandler")
    private LgoutHandler lgoutHandler;

    @Value("${springdoc.api-docs.path:}")
    private String API_DOCS_PATH;

    /**
     * 로그인시 사용할 암호화 로직
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 접속IP 차단 위해 사용할 custom AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new DreamdiaryAuthenticationProvider();
    }

    /**
     * 중복 로그인 방지:: logout 후 login 처리시 정상작동을 위함
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * 중복 로그인 방지:: WAS가 여러 개 있을 때 (session clustering)
     */
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    /**
     * 설정 정보
     */
    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring()
           // static 디렉터리의 하위 파일 목록은 인증 무시(=항상 통과 )
           .antMatchers("/css/**", "/js/**", "/media/**", "/font/**", "/lib/**", "/metronic/**", "/react/**", "/content/**", "/upfiles/public/**")
           // 에러 페이지
           .antMatchers(SiteUrl.ERROR + "/**")
           //// 비밀번호 만료시 비밀번호 변경 화면
           .antMatchers(SiteUrl.AUTH_LGN_PW_CHG_AJAX);
           //// 신규계정 신청 화면/기능 전체 접근 (+아이디 중복 체크)
           //.antMatchers(SiteUrl.USER_REQST_REG_FORM)
           //.antMatchers(SiteUrl.USER_REQST_REG_AJAX)
           //.antMatchers(SiteUrl.USER_ID_DUP_CHCK_AJAX);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 페이지 권한 설정
        http.authorizeRequests()
            // 로그인 화면 전체 접근
            .antMatchers(SiteUrl.AUTH_LGN_FORM)
            .permitAll()
            // static resource 전체 접근
            .antMatchers("/css/**", "/js/**", "/media/**", "/font/**", "/lib/**", "/metronic/**", "/react/**", "/content/**", "/upfiles/public/**")
            .permitAll()
            // API 접근에는 인증 적용하지 않음
            // TODO: inbound API 쪽에 토큰 인증 적용하기
            .antMatchers("/api/**")
            .permitAll()
            // Swagger
            .antMatchers(API_DOCS_PATH)
            .permitAll()
            // 이외 페이지 = 로그인 사용자만 접근
            .anyRequest()
            .authenticated();
        // .anyRequest().hasRole("MNGR");

        // 시큐리티에서 post 전송시 뭐시기..
        http.csrf()
            .disable();

        // remember-me 관련
        http.rememberMe()
            .key(REMEMBER_ME_KEY)
            .rememberMeParameter(REMEMBER_ME_PARAM)
            .tokenValiditySeconds(86400 * 30)
            .userDetailsService(authService)
            .authenticationSuccessHandler(lgnSuccessHandler);

        // 중복 로그인 방지
        http.sessionManagement()
            .maximumSessions(1)     // 최대 1개
            .maxSessionsPreventsLogin(false)        // true:: 나중에 접속한 사용자 로그인 방지, false:: 먼저 접속한 사용자 로그아웃 처리
            .expiredUrl(SiteUrl.AUTH_LGN_FORM + "?dupLgnAt=Y")
            .sessionRegistry(sessionRegistry());

        // Form 로그인 설정
        http.formLogin()
            .loginPage(SiteUrl.AUTH_LGN_FORM)
            .usernameParameter("userId")
            .passwordParameter("userPw")
            .loginProcessingUrl(SiteUrl.AUTH_LGN_PROC)
            .defaultSuccessUrl(SiteUrl.MAIN)
            .failureHandler(lgnFailureHandler)
            .successHandler(lgnSuccessHandler)
            .permitAll();

        // 로그아웃 설정
        http.logout()
            .logoutRequestMatcher(new AntPathRequestMatcher(SiteUrl.AUTH_LGOUT))
            .logoutSuccessUrl(SiteUrl.AUTH_LGN_FORM)
            .addLogoutHandler(lgoutHandler)
            .invalidateHttpSession(true);

        // 403(권한없는 주소 접근) 예외처리 핸들링
        http.exceptionHandling()
            .accessDeniedPage(SiteUrl.ERROR_ACCESS_DENIED);
    }
}
