package io.nicheblog.dreamdiary.web.service.jrnl.dream;

import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamTagEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import io.nicheblog.dreamdiary.web.service.cmm.cache.CacheEvictor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JrnlDreamCacheEvictor
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class JrnlDreamCacheEvictor
        implements CacheEvictor<Integer> {

    private final JrnlDreamService jrnlDreamService;

    /**
     * 관련 Cache Evict
     */
    @Override
    public void evict(final Integer key) throws Exception {
        // jrnl_day
        EhCacheUtils.evictCacheAll("jrnlDayList");
        // 태그가 삭제되었을 때 태그 목록 캐시 초기화 (부재시 삭제 데이터 조회)
        JrnlDreamDto jrnlDream = (JrnlDreamDto) EhCacheUtils.getObjectFromCache("jrnlDreamDtlDto", key);
        if (jrnlDream == null) {
            try {
                jrnlDream = jrnlDreamService.getDtlDto(key);
            } catch (Exception e) {
                jrnlDream = jrnlDreamService.getDeletedDtlDto(key);
                if (jrnlDream == null) return;
            }
        }
        // 상세 캐시 삭제
        EhCacheUtils.evictCache("jrnlDreamDtlDto", key);
        // 년도-월에 따른 캐시 삭제
        Integer yy = jrnlDream.getYy();
        Integer mnth = jrnlDream.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayDtlDto", jrnlDream.getJrnlDayNo());
        this.evictCacheForPeriod("jrnlDayList", yy, mnth);
        // jrnl_dream_tag
        this.evictCacheForPeriod("jrnlDreamTagList", yy, mnth);
        this.evictCacheForPeriod("jrnlDreamSizedTagList", yy, mnth);
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlDreamTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDreamContentTagEntity.class);
    }
}
