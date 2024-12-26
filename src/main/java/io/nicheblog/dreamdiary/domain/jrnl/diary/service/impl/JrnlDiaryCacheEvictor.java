package io.nicheblog.dreamdiary.domain.jrnl.diary.service.impl;

import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryContentTagEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryTagEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.model.JrnlDiaryDto;
import io.nicheblog.dreamdiary.domain.jrnl.diary.service.JrnlDiaryService;
import io.nicheblog.dreamdiary.global._common.cache.service.CacheEvictor;
import io.nicheblog.dreamdiary.global._common.cache.util.EhCacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JrnlDiaryCacheEvictor
 * <p>
 *  저널 일기 관련 캐시 evictor.
 * </p>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class JrnlDiaryCacheEvictor
        implements CacheEvictor<Integer> {

    private final JrnlDiaryService jrnlDiaryService;

    /**
     * 해당 컨텐츠 타입 관련 캐시를 제거한다.
     *
     * @param key 캐시에서 삭제할 대상 키
     * @throws Exception 캐시 삭제 과정에서 발생할 수 있는 예외
     */
    @Override
    public void evict(final Integer key) throws Exception {
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
        // jrnl_dream
        EhCacheUtils.evictCacheAll("jrnlDiaryList");
        EhCacheUtils.evictCache("jrnlDiaryDtlDto", key);
        // 년도-월에 따른 캐시 삭제
        final Integer yy = jrnlDiary.getYy();
        final Integer mnth = jrnlDiary.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayDtlDto", jrnlDiary.getJrnlDayNo());
        this.evictCacheForPeriod("jrnlDayList", yy, mnth);
        // jrnl_diary_tag
        this.evictCacheForPeriod("jrnlDiaryTagList", yy, mnth);
        this.evictCacheForPeriod("jrnlDiarySizedTagList", yy, mnth);
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlDiaryEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDiaryTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDiaryContentTagEntity.class);
    }
}