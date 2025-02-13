package io.nicheblog.dreamdiary.auth.security.config;

import io.nicheblog.dreamdiary.auth.jwt.filter.JwtAuthenticationFilter;
import io.nicheblog.dreamdiary.auth.oauth2.handler.OAuth2AuthenticationFailureHandler;
import io.nicheblog.dreamdiary.auth.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import io.nicheblog.dreamdiary.auth.security.handler.AjaxAwareAuthenticationEntryPoint;
import io.nicheblog.dreamdiary.auth.security.handler.LgoutHandler;
import io.nicheblog.dreamdiary.auth.security.handler.WebLgnFailureHandler;
import io.nicheblog.dreamdiary.auth.security.handler.WebLgnSuccessHandler;
import io.nicheblog.dreamdiary.auth.security.service.AuthService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * WebSecurityAdapter
 * <pre>
 *  Spring Security 설정
 *  웹사이트 URL 경로에 대한 인증을 설정한다.
 * </pre>
 * TODO: deprecated된 요소들 바꾸기.
 *
 * @author nichefish
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
@Log4j2
public class WebSecurityAdapter
        extends WebSecurityConfigurerAdapter {

    private final AuthService authService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;
    private final WebLgnFailureHandler webLgnFailureHandler;
    private final WebLgnSuccessHandler webLgnSuccessHandler;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;
    private final AjaxAwareAuthenticationEntryPoint ajaxAwareAuthenticationEntryPoint;
    private final LgoutHandler lgoutHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${springdoc.api-docs.path:}")
    private String API_DOCS_PATH;
    @Value("${remember-me.key}")
    private String REMEMBER_ME_KEY;
    @Value("${remember-me.param}")
    private String REMEMBER_ME_PARAM;

    /**
     * 빈 생성 ::중복 로그인 방지:: logout 후 login 처리시 정상작동을 위함
     *
     * @return {@link SessionRegistry} -- SessionRegistry 객체
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * 빈 생성 :: 중복 로그인 방지:: WAS가 여러 개 있을 때 처리 (session clustering)
     *
     * @return {@link ServletListenerRegistrationBean} -- ServletListenerRegistrationBean 객체
     */
    @Bean
    public static ServletListenerRegistrationBean<?> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }



    /**
     * Spring Security의 WebSecurity 설정을 구성합니다.
     * "HttpSecurity 앞단에 적용되며, 전체적으로 스프링 시큐리티의 영향권 밖에 있습니다."
     *
     * @param web WebSecurity 객체로, 웹 관련 보안 설정을 구성하는 데 사용됩니다
     * @throws Exception 보안 구성 중 발생할 수 있는 예외
     */
    @Override
    public void configure(final WebSecurity web) throws Exception {

        web.ignoring()
                // 세션 만료 처리 URL
                .antMatchers(Url.AUTH_EXPIRE_SESSION_AJAX)
                // static 디렉터리의 하위 파일 목록은 인증 무시(=항상 통과 )
                .antMatchers("/favicon.ico")
                .antMatchers("/robots.txt")
                // 에러 페이지
                .antMatchers(Url.ERROR + "/**")
                // 비밀번호 만료시 비밀번호 변경 화면
                .antMatchers(Url.AUTH_LGN_PW_CHG_AJAX)
                // 신규계정 신청 화면/기능 전체 접근 (+아이디 중복 체크)
                .antMatchers(Url.USER_REQST_REG_FORM)
                .antMatchers(Url.USER_REQST_REG_AJAX)
                .antMatchers(Url.USER_ID_DUP_CHK_AJAX)
                .antMatchers(Url.USER_EMAIL_DUP_CHK_AJAX);
    }

    /**
     * HTTP 보안 설정을 구성합니다.
     *
     * @param http HttpSecurity 객체로, HTTP 보안 관련 설정을 구성하는 데 사용됩니다
     * @throws Exception 보안 구성 중 발생할 수 있는 예외
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        // 1. Form 로그인 설정
        http.formLogin()
                .loginPage(Url.AUTH_LGN_FORM)
                .usernameParameter("userId")
                .passwordParameter("password")
                .loginProcessingUrl(Url.AUTH_LGN_PROC)
                .defaultSuccessUrl(Url.MAIN)
                .failureHandler(webLgnFailureHandler)
                .successHandler(webLgnSuccessHandler)
                .permitAll();

        // 2. JWT 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 3. OAuth2 로그인 활성화
        http.oauth2Login()
                .loginPage(Url.AUTH_LGN_FORM)
                .userInfoEndpoint()
                    .userService(oauth2UserService)
                .and()
                .successHandler(oauth2AuthenticationSuccessHandler)
                .failureHandler(oauth2AuthenticationFailureHandler);

        // 페이지 권한 설정
        http.authorizeRequests()
                // formLogin 설정만으로 로그인 페이지는 접근이 허용된다.
                // static resource 전체 접근

                .antMatchers(Constant.STATIC_PATHS)
                .permitAll()
                // API 접근에는 인증 적용하지 않음
                // TODO: inbound API 쪽에 토큰 인증 적용하기
                .antMatchers("/api/**")
                .permitAll()
                // OAUTH2 인증 관련 페이지
                .antMatchers("/oauth2/authorization/**", "/login/oauth2/code/**")
                .permitAll()
                // Swagger
                .antMatchers(API_DOCS_PATH)
                .permitAll()
                // WebSocket 엔드포인트에 대한 접근 허용
                .antMatchers("/chat/**")
                .permitAll()
                // 이외 페이지 = 로그인 사용자만 접근
                .anyRequest()
                .authenticated();
        // .anyRequest().hasRole("MNGR");

        // 시큐리티에서 post 전송시 뭐시기..
        // TODO : 나중에 추가하기. 일단은 우선순위 낮음
        http.csrf()
                .disable();

        // remember-me 관련
        http.rememberMe()
                .key(REMEMBER_ME_KEY)
                .rememberMeParameter(REMEMBER_ME_PARAM)
                .tokenValiditySeconds(86400 * 30)
                .userDetailsService(authService)
                .authenticationSuccessHandler(webLgnSuccessHandler);

        // 중복 로그인 방지
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)     // 최대 1개
                .maxSessionsPreventsLogin(false)        // true:: 나중에 접속한 사용자 로그인 방지, false:: 먼저 접속한 사용자 로그아웃 처리
                .expiredUrl(Url.AUTH_LGN_FORM + "?dupLgnAt=Y")
                .sessionRegistry(sessionRegistry());

        // 로그아웃 설정
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(Url.AUTH_LGOUT))
                .logoutUrl(Url.AUTH_LGOUT)
                .logoutSuccessUrl(Url.AUTH_LGN_FORM)
                .addLogoutHandler(lgoutHandler)
                .invalidateHttpSession(true);

        // 401/403 예외처리 핸들링
        http.exceptionHandling()
                .authenticationEntryPoint(ajaxAwareAuthenticationEntryPoint)
                .accessDeniedPage(Url.ERROR_ACCESS_DENIED);
    }

}
