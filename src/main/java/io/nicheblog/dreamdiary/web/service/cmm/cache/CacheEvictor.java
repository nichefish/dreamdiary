package io.nicheblog.dreamdiary.web.service.cmm.cache;

import io.nicheblog.dreamdiary.global.util.EhCacheUtils;

import java.io.Serializable;

/**
 * CacheEvictor
 * <pre>
 *   cacheEvictor 공통 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface CacheEvictor<Key extends Serializable> {

    void evict(Key key) throws Exception;

    /**
     * 캐시 이름에 대해서 기간 캐시 삭제 :: 메소드 분리
     */
    default void evictCacheForPeriod(final String cacheName, final String yy, final String mnth) {
        this.evictCacheForPeriod(cacheName, Integer.parseInt(yy), Integer.parseInt(mnth));
    }
    default void evictCacheForPeriod(final String cacheName, final Integer yy, final Integer mnth) {
        EhCacheUtils.evictCache(cacheName, yy + "_" + mnth);
        EhCacheUtils.evictCache(cacheName, yy + "_99");
        EhCacheUtils.evictCache(cacheName, "9999_99");
    }
}
