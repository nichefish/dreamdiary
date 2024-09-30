package io.nicheblog.dreamdiary.web.handler;

import io.nicheblog.dreamdiary.web.event.EhCacheEvictEvent;
import io.nicheblog.dreamdiary.web.service.cmm.cache.EhCacheEvictService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * EhCacheEvictEventListner
 * <pre>
 *  EhCache Evict 이벤트 처리 핸들러
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class EhCacheEvictEventListner {

    private final EhCacheEvictService ehCacheEvictService;

    /**
     * 조치자 추가
     */
    @EventListener
    public void handleEhCacheEvictvent(EhCacheEvictEvent event) throws Exception {
        // 조치자 추가
        ehCacheEvictService.evictClsfCache(event.getContentType(), event.getPostNo());
    }
}
