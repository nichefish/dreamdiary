package io.nicheblog.dreamdiary.global.config;

import org.ehcache.jsr107.EhcacheCachingProvider;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.cache.Caching;
import java.net.URL;

/**
 * EhCacheConfig
 * <pre>
 *  EhCache 관련 Config
 * </pre>
 *
 * @author nichefish
 */
@Configuration
public class EhCacheConfig {

    /**
     * jCache (=EhCache를 Spring의 캐시 추상화에 맞게 변환)
     */
    @Primary
    @Bean(name = "jCacheManager")
    public JCacheCacheManager jCacheCacheManager() {

        return new JCacheCacheManager(ehCacheManager());
    }

    /** ehCache -> jCache로의 변환을 거쳐 사용 */
    @Primary
    @Bean(name = "ehCacheManager")
    public javax.cache.CacheManager ehCacheManager() {
        URL myUrl = getClass().getClassLoader().getResource("ehcache.xml");
        XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
        EhcacheCachingProvider provider = (EhcacheCachingProvider) Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
        return provider.getCacheManager(provider.getDefaultURI(),  xmlConfig);
    }

}