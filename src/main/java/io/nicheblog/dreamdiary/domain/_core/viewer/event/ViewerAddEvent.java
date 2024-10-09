package io.nicheblog.dreamdiary.domain._core.viewer.event;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * ViewerAddEvent
 * <pre>
 *  컨텐츠 열람자 추가 이벤트. :: 메인 로직과 분리
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class ViewerAddEvent
        extends ApplicationEvent {

    /** 컨텐츠 복합키 */
    private final BaseClsfKey clsfKey;

    /* ----- */

    /**
     * 생성자
     * @param source 이 이벤트의 출처를 나타내는 객체
     * @param clsfKey 게시물의 고유 키를 포함하는 BaseClsfKey 객체
     */
    public ViewerAddEvent(
            final Object source,
            final BaseClsfKey clsfKey) {
        super(source);
        this.clsfKey = clsfKey;
    }
}
