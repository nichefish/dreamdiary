package io.nicheblog.dreamdiary.web.event;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * TagAddEvent
 * <pre>
 *  태그 추가 이벤트 :: 메인 로직과 분리
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class TagProcEvent
        extends ApplicationEvent {

    /** 컨텐츠 복합키 */
    private final BaseClsfKey clsfKey;
    /** 태그 */
    private final TagCmpstn tagCmpstn;

    /* ----- */

    /**
     * 생성자
     */
    public TagProcEvent(
            final Object source,
            final BaseClsfKey clsfKey,
            TagCmpstn tag
    ) {
        super(source);
        this.clsfKey = clsfKey;
        this.tagCmpstn = tag;
    }

    public TagProcEvent(
            final Object source,
            final BaseClsfKey clsfKey
    ) {
        super(source);
        this.clsfKey = clsfKey;
        this.tagCmpstn = null;
    }
}
