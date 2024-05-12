package io.nicheblog.dreamdiary.global.util;

import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
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
    @Resource
    private SessionFactory factory;

    private static CacheManager cacheManager;
    private static SessionFactory sessionFactory;

    /** static 맥락에서 사용할 수 있도록 bean 주입 */
    @PostConstruct
    private void init() {
        cacheManager = manager;
        sessionFactory = factory;
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
    public static void evictCache(final String cacheName, final Integer cacheKey) {
        evictCache(cacheName, cacheKey.toString());
    }
    public static void evictCache(final String cacheName, final String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.info("cache name {} does not exists.", cacheName);
            return;
        }
        cache.evict(cacheKey);
    }

    /**
     * 캐시 이름으로 해당 캐시 evict
     */
    public static void evictCacheAll(final String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.info("cache name {} does not exists.", cacheName);
            return;
        }
        cache.clear();
    }

    /**
     * 전체 캐시 evict
     */
    public static Boolean clearAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        return true;
    }

    /**
     * Hibernate Second Level 캐시 특정 엔티티 삭제
     */
    public static void clearL2Cache(Class<?> clazz) {
        org.hibernate.Cache cache = sessionFactory.getCache();
        cache.evictEntityData(clazz);
    }

    /**
     * Hibernate Second Level 캐시 전체 삭제
     */
    public static void clearL2Cache() {
        org.hibernate.Cache cache = sessionFactory.getCache();
        cache.evictAllRegions();
    }
}