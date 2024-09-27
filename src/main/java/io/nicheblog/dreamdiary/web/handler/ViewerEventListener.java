package io.nicheblog.dreamdiary.web.handler;

import io.nicheblog.dreamdiary.web.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.web.service.cmm.viewer.ViewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ViewerEventListener
 * <pre>
 *  컨탠츠 열람자 이벤트 처리 핸들러
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class ViewerEventListener {

    private final ViewerService viewerService;

    /**
     * 열람자 추가
     */
    @EventListener
    public void handleViewerAddEvent(ViewerAddEvent event) {
        // 열람자 추가
        viewerService.addViewer(event.getClsfKey());
    }
}
