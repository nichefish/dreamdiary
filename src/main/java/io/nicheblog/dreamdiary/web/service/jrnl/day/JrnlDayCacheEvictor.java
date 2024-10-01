package io.nicheblog.dreamdiary.web.service.jrnl.day;

import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayTagEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDto;
import io.nicheblog.dreamdiary.web.service.cmm.cache.CacheEvictor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JrnlDayCacheEvictor
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class JrnlDayCacheEvictor
        implements CacheEvictor<Integer> {

    private final JrnlDayService jrnlDayService;

    /**
     * 관련 Cache Evict
     */
    @Override
    public void evict(final Integer key) throws Exception {
        // jrnl_day
        EhCacheUtils.evictCacheAll("jrnlDayList");
        // 태그가 삭제되었을 때 태그 목록 캐시 초기화
        JrnlDayDto jrnlDay = (JrnlDayDto) EhCacheUtils.getObjectFromCache("jrnlDayDtlDto", key);
        if (jrnlDay == null) {
            try {
                jrnlDay = jrnlDayService.getDtlDto(key);
            } catch (Exception e) {
                jrnlDay = jrnlDayService.getDeletedDtlDto(key);
                if (jrnlDay == null) return;
            }
        }
        // 년도-월에 따른 캐시 삭제
        String yy = jrnlDay.getYy();
        String mnth = jrnlDay.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayDtlDto", key);
        this.evictCacheForPeriod("jrnlDayList", yy, mnth);
        // jrnl_day_tag
        this.evictCacheForPeriod("jrnlDayTagList", yy, mnth);
        this.evictCacheForPeriod("jrnlDaySizedTagList", yy, mnth);
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlDayTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDayContentTagEntity.class);
    }
}
