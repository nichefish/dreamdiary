package io.nicheblog.dreamdiary.global.config;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

/**
 * CacheConfig
 * <pre>
 *  Cache 관련 설정 커스터마이즈
 * </pre>
 *
 * @author nichefish
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cacheManager -> {
            cacheManager.createCache("dreamdiaryCache", new MutableConfiguration<>()
                    .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR))
                    .setStoreByValue(false)
                    .setStatisticsEnabled(true));
        };
    }
}