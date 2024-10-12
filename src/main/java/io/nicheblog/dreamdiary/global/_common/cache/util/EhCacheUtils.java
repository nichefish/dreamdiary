package io.nicheblog.dreamdiary.global._common.cache.util;

import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
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

    @Resource(name="jCacheManager")
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
    public static List<Cache> getActiveCacheList() {
        return cacheManager.getCacheNames()
                .stream()
                .map(cacheName -> cacheManager.getCache(cacheName))
                .collect(Collectors.toList());
    }

    /**
     * 캐시 목록 조회
     */
    public static Map<String, Object> getActiveCacheMap() {
        Map<String, Object> cacheContents = new HashMap<>();

        getActiveCacheList().stream()
                .forEach(cache -> {
                    String name = cache.getName();
                    Object object = cache.getNativeCache();
                    com.github.benmanes.caffeine.cache.Cache caffeineCache = (com.github.benmanes.caffeine.cache.Cache) object;

                    Map<Object, Object> obj = caffeineCache.asMap();
                    Map<Object, Object> cacheValue = new HashMap<>();
                    obj.keySet().forEach(key -> {
                        log.info(key);
                        if (key instanceof SimpleKey) key = "-";
                        cacheValue.put(key, caffeineCache.getIfPresent(key));
                    });
                    cacheContents.put(name, cacheValue);
                });
        return cacheContents;
    }

    /**
     * 캐시에서 키를 기반으로 오브젝트를 가져오는 메서드
     * @param cacheName 캐시 이름
     * @param cacheKey 캐시 키
     * @return 캐시에서 가져온 오브젝트, 캐시에 해당 키가 없는 경우 null 반환
     */
    public static Object getObjectFromCache(final String cacheName, final Object cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.info("Cache with name {} does not exist.", cacheName);
            return null;
        }
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper == null) {
            log.info("Object with key {} does not exist in cache {}.", cacheKey, cacheName);
            return null;
        }
        return valueWrapper.get();
    }

    /**
     * 캐시 이름의 특정 키 evict
     */
    public static void evictCache(final String cacheName, final Object cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.info("cache name {} does not exists.", cacheName);
            return;
        }
        cache.evict(cacheKey);
        log.info("cache name {} (key: {}) evicted.", cacheName, cacheKey);
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
        log.info("cache name {} cleared.", cacheName);
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
    public static void clearL2Cache(final Class<?> clazz) {
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