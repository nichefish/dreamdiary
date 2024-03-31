package io.nicheblog.dreamdiary.web.handler;

import io.nicheblog.dreamdiary.web.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.web.service.cmm.viewer.ViewerService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ViewerEventListener
 * <pre>
 *  컨탠츠 열람자 이벤트 처리 핸들러
 * </pre>
 *
 * @author nichefish
 */
@Component("viewerEventListener")
public class ViewerEventListener {

    @Resource
    private ViewerService viewerService;

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    public void handleViewerAddEvent(ViewerAddEvent viewerAddEvent) {
        // 활동 로그 (로그인) 로깅 처리
        viewerService.addViewer(viewerAddEvent.getClsfKey());
    }
}
