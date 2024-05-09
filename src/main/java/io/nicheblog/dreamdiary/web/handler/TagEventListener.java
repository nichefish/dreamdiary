package io.nicheblog.dreamdiary.web.handler;

import io.nicheblog.dreamdiary.web.event.TagProcEvent;
import io.nicheblog.dreamdiary.web.service.cmm.tag.ContentTagService;
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
@Component
public class TagEventListener {

    @Resource(name="tagService")
    private TagService tagService;
    @Resource(name="contentTagService")
    private ContentTagService contentTagService;

    /**
     * 태그 처리
     */
    @EventListener
    public void handleTagProcEvent(TagProcEvent event) throws Exception {

        // 태그 객체 없이 키값만 넘어오면? 컨텐츠 삭제.
        boolean isContentDelete = (event.getTagCmpstn() == null);
        if (isContentDelete) {
            // 기존 컨텐츠-태그 전부 삭제
            contentTagService.delExistingContentTags(event.getClsfKey());
        } else {
            // 태그 처리
            tagService.procTags(event.getClsfKey(), event.getTagCmpstn());
        }
        // 태그테이블 refresh (연관관계 없는 태그 삭제)
        tagService.deleteNoRefTags();
    }
}
