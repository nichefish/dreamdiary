package io.nicheblog.dreamdiary.global.config;

import io.nicheblog.dreamdiary.global.handler.UTF8DecodeResourceResolver;
import io.nicheblog.dreamdiary.global.interceptor.FreemarkerInterceptor;
import io.nicheblog.dreamdiary.web.SiteUrl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
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
public class WebMvcContextConfig
        implements WebMvcConfigurer {

    @Resource(name = "freemarkerInterceptor")
    private FreemarkerInterceptor freemarkerInterceptor;

    private static final List<String> STATIC_RESOURCES_URL_PATTERN = List.of("/css/**", "/js/**", "/img/**", "/font/**", "/lib/**", "/metronic/**", "/react/**", "/content/**", "/upfiles/**");

    /**
     * Device 정보 resolver
     */
    @Bean
    public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
        return new DeviceHandlerMethodArgumentResolver();
    }

    /**
     * Device 정보 resolver
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(deviceHandlerMethodArgumentResolver());
    }

    /**
     * 프로젝트 외부 이미지 연결
     * 업로드파일 + vod 폴더 통째로 연결 (업로드 링크 위해)
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 파일 업로드 경로
        String upfileContextPath = "/upfiles/**";
        String upfileResourcePath = "file:/webapps/intranet/upfiles/";
        registry.addResourceHandler(upfileContextPath)
                .addResourceLocations(upfileResourcePath)
                .resourceChain(true)
                .addResolver(new UTF8DecodeResourceResolver());
        // workspace 경로
        String workspaceContextPath = "/workspace/**";
        String workspaceResourcePath = "file:/webapps/intranet/workspace/";
        registry.addResourceHandler(workspaceContextPath)
                .addResourceLocations(workspaceResourcePath)
                .resourceChain(true)
                .addResolver(new UTF8DecodeResourceResolver());
        // storage 경로
        String storageContextPath = "/storage/**";
        String storageResourcePath = "file:/webapps/intranet/storage/";
        registry.addResourceHandler(storageContextPath)
                .addResourceLocations(storageResourcePath)
                .resourceChain(true)
                .addResolver(new UTF8DecodeResourceResolver());
        // vod-storage 경로
        String vodContextPath = "/vod-storage/**";
        String vodResourcePath = "file:/webapps/intranet/vod-storage/";
        registry.addResourceHandler(vodContextPath)
                .addResourceLocations(vodResourcePath)
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
        registry.addResourceHandler(staticContextPath)
                .addResourceLocations(orglStaticPath, reactPath, reactStaticPath)
                .resourceChain(true)
                .addResolver(new UTF8DecodeResourceResolver());
    }

    /**
     * interceptor 추가
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(freemarkerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(STATIC_RESOURCES_URL_PATTERN)
                .excludePathPatterns(SiteUrl.AUTH_LGN_FORM)
                // .excludePathPatterns(SiteUrl.USER_ID_DUP_CHCK_AJAX)
                // .excludePathPatterns(SiteUrl.USER_REQST_REG_FORM)
                // .excludePathPatterns(SiteUrl.USER_REQST_REG_AJAX)
                .excludePathPatterns("/api/**");
        // device 감지 관련 인터셉터 수동 추가
        registry.addInterceptor(new DeviceResolverHandlerInterceptor());
    }
}
