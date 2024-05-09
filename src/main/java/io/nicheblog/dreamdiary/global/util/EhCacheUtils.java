package io.nicheblog.dreamdiary.global.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * EhCacheUtils
 * <pre>
 *  ehCache 수동 적용 유틸리티 모듈
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class EhCacheUtils {

    @Resource(name="cacheManager")
    CacheManager manager;

    private static CacheManager cacheManager;

    /** static 맥락에서 사용할 수 있도록 bean 주입 */
    @PostConstruct
    private void init() {
        cacheManager = manager;
    }

    /**
     * 캐시 목록 조회
     */
    public static List<String> chckActiveCacheNm() {
        return new ArrayList<>(cacheManager.getCacheNames());
    }

    /**
     * 캐시 목록 조회
     */
    public static List<Cache> chckActiveCaches() {
        return cacheManager.getCacheNames()
                .stream()
                .map(cacheName -> cacheManager.getCache(cacheName))
                .collect(Collectors.toList());
    }

    /**
     * 캐시 이름의 특정 키 evict
     */
    public static void evictSingleCacheValue(final String cacheName, final String cacheKey) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).evict(cacheKey);
    }

    /**
     * 캐시 이름으로 해당 캐시 evict
     */
    public static void evictAllCacheValues(final String cacheName) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
    }

    /**
     * 전체 캐시 evict
     */
    public static Boolean clearAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        return true;
    }
}