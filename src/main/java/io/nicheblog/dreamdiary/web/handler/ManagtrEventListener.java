package io.nicheblog.dreamdiary.web.handler;

import io.nicheblog.dreamdiary.web.event.ManagtrAddEvent;
import io.nicheblog.dreamdiary.web.service.cmm.managt.ManagtrService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ManagtrEventListener
 * <pre>
 *  컨탠츠 열람자 이벤트 처리 핸들러
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class ManagtrEventListener {

    private final ManagtrService managtrService;

    /**
     * 조치자 추가
     */
    @EventListener
    public void handleManagtrAddEvent(ManagtrAddEvent event) {
        // 조치자 추가
        managtrService.addManagtr(event.getClsfKey());
    }
}
