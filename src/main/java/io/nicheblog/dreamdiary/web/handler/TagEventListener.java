package io.nicheblog.dreamdiary.web.handler;

import io.nicheblog.dreamdiary.web.event.TagAddEvent;
import io.nicheblog.dreamdiary.web.service.cmm.tag.TagService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * TagEventListener
 * <pre>
 *  태그 관련 이벤트 처리 핸들러
 * </pre>
 *
 * @author nichefish
 */
@Component("tagEventListener")
public class TagEventListener {

    @Resource
    private TagService tagService;

    /**
     * 태그 처리
     */
    @EventListener
    public void handleTagAddEvent(TagAddEvent tagAddEvent) {
        // 활동 로그 (로그인) 로깅 처리
        tagService.procTags(tagAddEvent.getClsfKey(), tagAddEvent.getTagCmpstn());
    }
}
