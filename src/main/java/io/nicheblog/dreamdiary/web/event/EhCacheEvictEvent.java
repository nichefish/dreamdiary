package io.nicheblog.dreamdiary.web.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * EhCacheEvictEvent
 * <pre>
 *  EhCache 캐시 evict 이벤트 :: 메인 로직과 분리
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class EhCacheEvictEvent
        extends ApplicationEvent {

    /** 컨텐츠 타입 */
    private final Integer postNo;
    /** 컨텐츠 타입 */
    private final String contentType;

    /* ----- */

    /**
     * 생성자
     */
    public EhCacheEvictEvent(
            final Object source,
            final Integer postNo,
            final String contentType
    ) {
        super(source);
        this.postNo = postNo;
        this.contentType = contentType;
    }
}
