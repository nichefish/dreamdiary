package io.nicheblog.dreamdiary.global._common.redis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * RedisUtils
 * <pre>
 *  Redis 관련 유틸리티 클래스
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * setData
     */
    public void setData(final String key, final String value, final Long expiredTime){
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    /**
     * getData
     */
    public String getData(final String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * deleteData
     */
    public void deleteData(final String key){
        redisTemplate.delete(key);
    }
}
