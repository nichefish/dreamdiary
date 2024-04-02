package io.nicheblog.dreamdiary.web.event;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * TagDelEvent
 * <pre>
 *  태그 삭제 이벤트 :: 메인 로직과 분리
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class TagDelEvent
        extends ApplicationEvent {

    /** 컨텐츠 복합키 */
    private final BaseClsfKey clsfKey;

    /* ----- */

    /**
     * 생성자
     */
    public TagDelEvent(
            final Object source,
            final BaseClsfKey clsfKey,
            TagCmpstn tag
    ) {
        super(source);
        this.clsfKey = clsfKey;
    }
}
