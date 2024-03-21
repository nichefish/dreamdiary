package io.nicheblog.dreamdiary.cmm.util;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * EhCacheService
 * ehCache 수동 적용 서비스 모듈
 * @author nichefish
 */
@UtilityClass
@Log4j2
public class EhCacheUtil {

    @Resource(name="cacheManager")
    CacheManager cacheManager;

    /**
     * 캐시 목록 조회
     */
    public List<String> chckActiveCaches() {
        return new ArrayList<>(cacheManager.getCacheNames());
    }

    /**
     * 캐시 이름의 특정 키 evict
     */
    public void evictSingleCacheValue(final String cacheName, final String cacheKey) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).evict(cacheKey);
    }

    /**
     * 캐시 이름으로 해당 캐시 evict
     */
    public void evictAllCacheValues(final String cacheName) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
    }

    /**
     * 전체 캐시 evict
     */
    public Boolean clearAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        return true;
    }
}