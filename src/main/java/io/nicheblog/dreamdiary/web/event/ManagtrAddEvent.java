package io.nicheblog.dreamdiary.web.event;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * ManagtrAddEvent
 * <pre>
 *  조치자Managtr 추가 이벤트 :: 메인 로직과 분리
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class ManagtrAddEvent
        extends ApplicationEvent {

    /** 컨텐츠 복합키 */
    private final BaseClsfKey clsfKey;

    /* ----- */

    /**
     * 생성자
     */
    public ManagtrAddEvent(
            final Object source,
            final BaseClsfKey clsfKey) {
        super(source);
        this.clsfKey = clsfKey;
    }
}
