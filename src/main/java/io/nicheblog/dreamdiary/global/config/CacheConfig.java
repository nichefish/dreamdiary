package io.nicheblog.dreamdiary.global.config;

import lombok.SneakyThrows;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import javax.cache.Caching;
import java.net.URL;

/**
 * CacheConfig
 * <pre>
 *  Cache 관련 설정 커스터마이즈
 * </pre>
 *
 * @author nichefish
 */
@Configuration
public class CacheConfig {

    @Resource(name = "redisProperty")
    private RedisProperty redisProperty;

    @Bean
    @SneakyThrows
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperty.getHost());
        redisStandaloneConfiguration.setPort(redisProperty.getPort());
        redisStandaloneConfiguration.setPassword(redisProperty.getPassword());
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // 일반적인 key:value의 경우 시리얼라이저
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        // Hash를 사용할 경우 시리얼라이저
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        // 모든 경우
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Primary
    @Bean(name = "cacheManager")
    public CacheManager redisCacheManager() {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        RedisSerializer.json()));

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(cacheConfiguration);

        return builder.build();
    }

    @Bean
    public JCacheCacheManager jCacheCacheManager() {
        return new JCacheCacheManager(ehCacheManager());
    }

    @Bean
    public javax.cache.CacheManager ehCacheManager() {
        URL myUrl = getClass().getClassLoader().getResource("ehcache.xml");
        org.ehcache.config.Configuration xmlConfig = new XmlConfiguration(myUrl);
        EhcacheCachingProvider provider = (EhcacheCachingProvider) Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
        return provider.getCacheManager(provider.getDefaultURI(),  xmlConfig);
    }

    @Bean
    public CacheResolver cacheResolver() {
        return new CustomCacheResolver(jCacheCacheManager(), redisCacheManager());
    }
}