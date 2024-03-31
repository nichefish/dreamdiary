package io.nicheblog.dreamdiary.web.event;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * TagEvent
 * <pre>
 *  컨텐츠 조회자(viewer) 추가 이벤트 :: 메인 로직과 분리
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class TagEvent
        extends ApplicationEvent {

    /** 컨텐츠 복합키 */
    private final BaseClsfKey clsfKey;
    /** 태그 */
    private final TagCmpstn tagCmpstn;

    /* ----- */

    /**
     * 생성자
     */
    public TagEvent(
            final Object source,
            final BaseClsfKey clsfKey,
            TagCmpstn tag
    ) {
        super(source);
        this.clsfKey = clsfKey;
        this.tagCmpstn = tag;
    }
}
