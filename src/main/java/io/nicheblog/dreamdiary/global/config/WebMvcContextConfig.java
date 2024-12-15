package io.nicheblog.dreamdiary.global.config;

import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.auth.interceptor.CsrfInterceptor;
import io.nicheblog.dreamdiary.global.handler.UTF8DecodeResourceResolver;
import io.nicheblog.dreamdiary.global.interceptor.CookieInterceptor;
import io.nicheblog.dreamdiary.global.interceptor.FreemarkerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvcContextConfig
 * <pre>
 *  interceptor 설정
 * </pre>
 *
 * @author nichefish
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcContextConfig
        implements WebMvcConfigurer {

    private final FreemarkerInterceptor freemarkerInterceptor;
    private final CookieInterceptor cookieInterceptor;
    private final CsrfInterceptor csrfInterceptor;

    private static final List<String> STATIC_RESOURCES_URL_PATTERN = List.of("/css/**", "/js/**", "/media/**", "/font/**", "/lib/**", "/metronic/**", "/react/**", "/content/**", "/upfile/**");

    /**
     * 모든 /api/** 경로에 대하여 CORS 허용 설정 추가
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("GET", "POST")
                .allowedOrigins("*")
                .allowedHeaders("*");
    }

    /**
     * 프로젝트 외부 이미지 연결
     * 업로드파일 + vod 폴더 통째로 연결 (업로드 링크 위해)
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 파일 업로드 경로
        String upfileContextPath = "/upfile/**";
        String upfileResourcePath = "upfile/";
        registry.addResourceHandler(upfileContextPath)
                .addResourceLocations(upfileResourcePath)
                .resourceChain(true)
                .addResolver(new UTF8DecodeResourceResolver());
        // workspace 경로
        String workspaceContextPath = "/flsys/**";
        String workspaceResourcePath = "flsys/";
        registry.addResourceHandler(workspaceContextPath)
                .addResourceLocations(workspaceResourcePath)
                .resourceChain(true)
                .addResolver(new UTF8DecodeResourceResolver());
        // react 경로 = 기본경로에 추가로 동작하도록
        String reactContextPath = "/react/**";
        String reactResourcePath = "classpath:/react/";
        registry.addResourceHandler(reactContextPath)
                .addResourceLocations(reactResourcePath)
                .resourceChain(true)
                .addResolver(new UTF8DecodeResourceResolver());
        // 기본 static 경로
        String staticContextPath = "/static/**";
        String orglStaticPath = "classpath:/static/";
        String reactPath = "classpath:/static/react/";
        String reactStaticPath = "classpath:/static/react/static/";
        String externalStaticPath = "static/";
        registry.addResourceHandler(staticContextPath)
                .addResourceLocations(orglStaticPath, reactPath, reactStaticPath, externalStaticPath)
                .resourceChain(true)
                .addResolver(new UTF8DecodeResourceResolver());
    }

    /**
     * interceptor 추가
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // freemarker interceptor
        // 화면 조회에만 적용, ajax 및 기타 작동에는 적용 안함
        registry.addInterceptor(freemarkerInterceptor)
                /* 페이지 접근에 대해서만 처리 */
                .addPathPatterns("/")
                .addPathPatterns("/**/*.do")
                /* 스태틱 자원 경로의 경우 처리하지 않음 */
                .excludePathPatterns(STATIC_RESOURCES_URL_PATTERN)
                /* 로그인 화면 경로 제외 */
                .excludePathPatterns(Url.AUTH_LGN_FORM)
                /* 에러 화면 경로 제외 */
                .excludePathPatterns(Url.ERROR)
                .excludePathPatterns(Url.ERROR + "/**")
                .excludePathPatterns("/api/**")
                /* 파일 다운로드의 경우 처리하지 않음 */
                .excludePathPatterns("/**/*Download.do")
                /* ajax 호출의 경우 처리하지 않음 */
                .excludePathPatterns("/**/*Ajax.do");

        // 쿠키 관련 인터셉터 수동 추가
        registry.addInterceptor(cookieInterceptor)
                /* 페이지 접근에 대해서만 처리 */
                .addPathPatterns("/")
                .addPathPatterns("/**/*.do")
                /* 스태틱 자원 경로의 경우 처리하지 않음 */
                .excludePathPatterns(STATIC_RESOURCES_URL_PATTERN);

        // CSRF 관련 인터셉터 수동 추가
        registry.addInterceptor(csrfInterceptor)
                /* 페이지 접근에 대해서만 처리 */
                .addPathPatterns("/")
                .addPathPatterns("/**/*.do")
                /* 스태틱 자원 경로의 경우 처리하지 않음 */
                .excludePathPatterns(STATIC_RESOURCES_URL_PATTERN);

        // device 감지 관련 인터셉터 수동 추가
        registry.addInterceptor(new DeviceResolverHandlerInterceptor());
    }
}
