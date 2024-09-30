package io.nicheblog.dreamdiary.web.service.jrnl.sumry;

import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.sumry.JrnlSumryDto;
import io.nicheblog.dreamdiary.web.service.cmm.cache.CacheEvictor;
import org.springframework.stereotype.Component;

/**
 * JrnlSumryCacheEvictor
 *
 * @author nichefish
 */
@Component
public class JrnlSumryCacheEvictor
        implements CacheEvictor<Integer> {

    /**
     * 관련 Cache Evict
     */
    @Override
    public void evict(Integer key) throws Exception {
        // 목록 캐시 초기화
        EhCacheUtils.evictCacheAll("jrnlSumryList");
        EhCacheUtils.evictCacheAll("jrnlTotalSumry");
        // 상세 캐시 초기화
        EhCacheUtils.evictCache("jrnlSumryDtl", key);
        JrnlSumryDto jrnlSumry = (JrnlSumryDto) EhCacheUtils.getObjectFromCache("jrnlSumryDtl", key);
        if (jrnlSumry != null) EhCacheUtils.evictCache("jrnlSumryDtlByYy", jrnlSumry.getYy());
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlSumryEntity.class);
    }
}
