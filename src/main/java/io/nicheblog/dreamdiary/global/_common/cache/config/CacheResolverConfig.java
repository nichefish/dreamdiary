package io.nicheblog.dreamdiary.global._common.cache.config;

import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

import javax.annotation.Resource;

/**
 * CacheResolverConfig
 * <pre>
 *  Cache 관련 설정 커스터마이즈 (공톨 Resolver 부분 분리)
 * </pre>
 *
 * @author nichefish
 */
@Configuration
public class CacheResolverConfig {

    @Resource
    private JCacheCacheManager jCacheCacheManager;
    @Resource
    private RedisCacheManager redisCacheManager;

    /**
     * 빈 등록:: cacheResolver
     */
    @Bean
    public CacheResolver cacheResolver() {
        return new CustomCacheResolver(jCacheCacheManager, redisCacheManager);
    }
}