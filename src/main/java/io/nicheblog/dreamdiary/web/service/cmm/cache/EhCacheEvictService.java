package io.nicheblog.dreamdiary.web.service.cmm.cache;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.web.service.jrnl.day.JrnlDayCacheEvictor;
import io.nicheblog.dreamdiary.web.service.jrnl.diary.JrnlDiaryCacheEvictor;
import io.nicheblog.dreamdiary.web.service.jrnl.dream.JrnlDreamCacheEvictor;
import io.nicheblog.dreamdiary.web.service.jrnl.sumry.JrnlSumryCacheEvictor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * CacheEvictService
 * <pre>
 *  캐시 Evict 서비스 모듈
 *  (여기저기 반복되는 공통로직 분리 위한 클래스)
 * </pre>
 *
 * @author nichefish
 */
@Service("cacheEvictService")
@RequiredArgsConstructor
@Log4j2
public class EhCacheEvictService {

    private final JrnlDayCacheEvictor jrnlDayCacheEvictor;
    private final JrnlDiaryCacheEvictor jrnlDiaryCacheEvictor;
    private final JrnlDreamCacheEvictor jrnlDreamCacheEvictor;
    private final JrnlSumryCacheEvictor jrnlSumryCacheEvictor;

    // CacheEvictor를 매핑하는 Map
    private final Map<String, CacheEvictor<Integer>> evictorMap = new HashMap<>();

    @PostConstruct
    private void initEvictorMap() {
        evictorMap.put(ContentType.JRNL_DAY.key, jrnlDayCacheEvictor);
        evictorMap.put(ContentType.JRNL_DIARY.key, jrnlDiaryCacheEvictor);
        evictorMap.put(ContentType.JRNL_DREAM.key, jrnlDreamCacheEvictor);
        evictorMap.put(ContentType.JRNL_SUMRY.key, jrnlSumryCacheEvictor);
    }

    /**
     * 관련 캐시 처리
     */
    public void evictClsfCache(final String refContentType, final Integer refPostNo) throws Exception {
        CacheEvictor<Integer> evictor = evictorMap.get(refContentType);
        if (evictor == null) {
            log.warn("No CacheEvictor found for ContentType: {}", refContentType);
            return;
        }
        evictor.evict(refPostNo);
    }
}