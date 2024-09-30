package io.nicheblog.dreamdiary.web.service.cmm.cache;

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
    void evict(Key key);
}
