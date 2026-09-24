package io.nicheblog.dreamdiary.feature.prospective.keep.service.strategy;

import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.global.util.TransactionHookUtils;
import io.nicheblog.dreamdiary.infrastructure.cache.util.EhCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProspectiveKeepCacheEvictor
 * <p>
 *  Prospective Keep 관련 캐시 evictor.
 * </p>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class ProspectiveKeepCacheEvictor {

    /**
     * 트랜잭션 커밋 뒤 Keep 상세·태그 캐시를 제거한다.
     *
     * @param username Keep 소유 계정명
     * @param id Keep ID
     */
    public void evictAfterCommit(final String username, final Integer id) throws Exception {
        if (username == null || id == null) return;
        TransactionHookUtils.runAfterCommitOrNow(
                () -> this.evict(username, id),
                e -> log.error("Prospective Keep cache invalidation failed [{}]: {}", id, e.getMessage(), e)
        );
    }

    /**
     * 해당 컨텐츠 타입 관련 캐시를 제거한다.
     *
     * @param username Keep 소유 계정명
     * @param id Keep ID
     */
    @Transactional
    public void evict(final String username, final Integer id) throws Exception {
        final ContentType refContentType = ContentType.PROSPECTIVE_KEEP;
        try {
            // prospective_keep :: 활성 리스트는 캐시하지 않으므로(매 조회 신선) 상세·태그 캐시만 제거한다.
            EhCacheUtils.evictUserCacheByKey("prospectiveKeepDetailDtoByUser", username, id);
            // 태그 캐시 처리
            EhCacheUtils.evictCacheByKey("tagContentEntityListByRef", id + "_PROSPECTIVE_KEEP");
        } catch (final Exception e) {
            log.error("CacheEvictor error [{}]: {}", refContentType, e.getMessage(), e);
            throw e;
        }
    }
}
