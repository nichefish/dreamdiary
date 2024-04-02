package io.nicheblog.dreamdiary.web.handler;

import io.nicheblog.dreamdiary.web.event.ManagtrAddEvent;
import io.nicheblog.dreamdiary.web.service.cmm.managt.ManagtrService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ManagtrEventListener
 * <pre>
 *  컨탠츠 열람자 이벤트 처리 핸들러
 * </pre>
 *
 * @author nichefish
 */
@Component("managtrEventListener")
public class ManagtrEventListener {

    @Resource
    private ManagtrService managtrService;

    /**
     * 조치자 추가
     */
    @EventListener
    public void handleManagtrAddEvent(ManagtrAddEvent managtrAddEvent) {
        // 조치자 추가
        managtrService.addManagtr(managtrAddEvent.getClsfKey());
    }
}
