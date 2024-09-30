package io.nicheblog.dreamdiary.web.service.jrnl.diary;

import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryTagEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.diary.JrnlDiaryDto;
import io.nicheblog.dreamdiary.web.service.cmm.cache.CacheEvictor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JrnlDiaryCacheEvictor
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class JrnlDiaryCacheEvictor
        implements CacheEvictor<Integer> {

    private final JrnlDiaryService jrnlDiaryService;

    /**
     * 관련 Cache Evict
     */
    @Override
    public void evict(Integer key) throws Exception {
        // jrnl_day
        EhCacheUtils.evictCacheAll("jrnlDayList");
        // 태그가 삭제되었을 때 태그 목록 캐시 초기화
        JrnlDiaryDto jrnlDiary = (JrnlDiaryDto) EhCacheUtils.getObjectFromCache("jrnlDiaryDtlDto", key);
        if (jrnlDiary == null) {
            try {
                jrnlDiary = jrnlDiaryService.getDtlDto(key);
            } catch (Exception e) {
                jrnlDiary = jrnlDiaryService.getDeletedDtlDto(key);
                if (jrnlDiary == null) return;
            }
        }
        // 상세 캐시 삭제
        EhCacheUtils.evictCache("jrnlDiaryDtlDto", key);
        // 년도-월에 따른 캐시 삭제
        Integer yy = jrnlDiary.getYy();
        Integer mnth = jrnlDiary.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayDtlDto", jrnlDiary.getJrnlDayNo());
        this.evictCacheForPeriod("jrnlDayList", yy, mnth);
        // jrnl_diary_tag
        this.evictCacheForPeriod("jrnlDiaryTagList", yy, mnth);
        this.evictCacheForPeriod("jrnlDiarySizedTagList", yy, mnth);
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlDiaryTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDiaryContentTagEntity.class);
    }
}
