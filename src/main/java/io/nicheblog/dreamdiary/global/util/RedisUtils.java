package io.nicheblog.dreamdiary.global.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * RedisUtils
 * <pre>
 *  Redis 관련 유틸리티 클래스
 * </pre>
 *
 * @author nichefish
 */
@Service
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * setData
     */
    public void setData(String key, String value,Long expiredTime){
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    /**
     * getData
     */
    public String getData(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * deleteData
     */
    public void deleteData(String key){
        redisTemplate.delete(key);
    }
}
