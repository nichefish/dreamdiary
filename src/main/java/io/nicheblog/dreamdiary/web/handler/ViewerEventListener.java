package io.nicheblog.dreamdiary.web.handler;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.web.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.web.service.cmm.viewer.ViewerService;
import org.apache.commons.lang3.StringUtils;
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
@Component
public class ViewerEventListener {

    @Resource
    private ViewerService viewerService;

    /**
     * 열람자 추가
     */
    @EventListener
    public void handleViewerAddEvent(ViewerAddEvent event) {
        // 열람자 추가
        viewerService.addViewer(event.getClsfKey());
    }
}
