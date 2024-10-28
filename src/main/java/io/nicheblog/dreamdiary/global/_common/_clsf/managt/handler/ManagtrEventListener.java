package io.nicheblog.dreamdiary.global._common._clsf.managt.handler;

import io.nicheblog.dreamdiary.global._common._clsf.managt.event.ManagtrAddEvent;
import io.nicheblog.dreamdiary.global._common._clsf.managt.service.ManagtrService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ManagtrEventListener
 * <pre>
 *  컨탠츠 조치자 이벤트 처리 핸들러.
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class ManagtrEventListener {

    private final ManagtrService managtrService;

    /**
     * 조치자 추가.
     *
     * @param event 처리할 이벤트 객체
     */
    @EventListener
    public void handleManagtrAddEvent(ManagtrAddEvent event) {
        // 조치자 추가
        managtrService.addManagtr(event.getClsfKey());
    }
}
