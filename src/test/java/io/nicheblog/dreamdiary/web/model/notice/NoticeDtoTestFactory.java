package io.nicheblog.dreamdiary.web.model.notice;

import io.nicheblog.dreamdiary.global.ContentType;
import lombok.experimental.UtilityClass;

/**
 * NoticeDtoTestFactory
 * <pre>
 *  공지사항 테스트 Dto 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class NoticeDtoTestFactory {

    /**
     * 공지사항 상세 Dto 생성
     */
    public static NoticeDto.DTL createNoticeDtlDto() throws Exception {
        return NoticeDto.DTL.builder()
                .postNo(0)
                .contentType(ContentType.NOTICE.key)
                .popupYn("Y")
                .title("test_title")
                .cn("test_cn")
                .ctgrCd("test_ctgr_cd")
                .build();
    }
}
