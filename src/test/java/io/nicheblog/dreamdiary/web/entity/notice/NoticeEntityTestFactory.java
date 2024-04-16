package io.nicheblog.dreamdiary.web.entity.notice;

import io.nicheblog.dreamdiary.global.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * NoticeEntityTestFactory
 * <pre>
 *  공지사항 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class NoticeEntityTestFactory {

    /**
     * 공지사항 Entity 생성
     */
    public static NoticeEntity createNoticeEntity() throws Exception {
        return NoticeEntity.builder()
                .postNo(0)
                .contentType(ContentType.NOTICE.key)
                .popupYn("Y")
                .title("test_title")
                .cn("test_cn")
                .ctgrCd("test_ctgr_cd")
                .build();
    }
}
