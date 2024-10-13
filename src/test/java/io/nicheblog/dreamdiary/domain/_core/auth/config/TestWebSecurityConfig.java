package io.nicheblog.dreamdiary.domain._core.auth.config;

import io.nicheblog.dreamdiary.global.Url;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * TestWebSecurityConfig
 * <pre>
 *  테스트용 Spring Security 설정
 * </pre>
 *
 * @author nichefish
 */
@Configuration
@Profile("test")
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Log4j2
public class TestWebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 웹사이트 URL 경로에 대한 인증을 설정한다.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
    web.ignoring()
            // 로그인 화면 인증 무시
            .antMatchers(Url.AUTH_LGN_FORM)
            // static 디렉터리의 하위 파일 목록은 인증 무시(=항상 통과 )
            .antMatchers("/css/**", "/js/**", "/media/**", "/font/**", "/lib/**", "/metronic/**", "/react/**", "/content/**", "/upfile/public/**")
            .antMatchers("/favicon.ico")
            .antMatchers("/robots.txt")
            // 에러 페이지
            .antMatchers(Url.ERROR + "/**")
            // 비밀번호 만료시 비밀번호 변경 화면
            .antMatchers(Url.AUTH_LGN_PW_CHG_AJAX)
            // 신규계정 신청 화면/기능 전체 접근 (+아이디 중복 체크)
            .antMatchers(Url.USER_REQST_REG_FORM)
            .antMatchers(Url.USER_REQST_REG_AJAX)
            .antMatchers(Url.USER_ID_DUP_CHK_AJAX);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 페이지 권한 설정
        http.authorizeRequests()
                // 로그인 화면 전체 접근
                .antMatchers(Url.AUTH_LGN_FORM)
                .permitAll()
                // static resource 전체 접근
                .antMatchers("/css/**", "/js/**", "/media/**", "/font/**", "/lib/**", "/metronic/**", "/react/**", "/content/**", "/upfile/public/**")
                .permitAll()
                // API 접근에는 인증 적용하지 않음
                // TODO: inbound API 쪽에 토큰 인증 적용하기
                .antMatchers("/api/**")
                .permitAll()
                // 이외 페이지 = 로그인 사용자만 접근
                .anyRequest()
                .authenticated();
        // .anyRequest().hasRole("MNGR");

        // 시큐리티에서 post 전송시 뭐시기..
        http.csrf()
                .disable();


        // 403(권한없는 주소 접근) 예외처리 핸들링
        http.exceptionHandling()
                .accessDeniedPage(Url.ERROR_ACCESS_DENIED);
    }

}
