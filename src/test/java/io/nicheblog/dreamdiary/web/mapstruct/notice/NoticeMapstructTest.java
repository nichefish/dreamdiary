package io.nicheblog.dreamdiary.web.mapstruct.notice;

import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDtoTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * NoticeMapstructTest
 * <pre>
 *  공지사항 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
class NoticeMapstructTest {

    private final NoticeMapstruct noticeMapstruct = NoticeMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        NoticeDto.DTL noticeDto = NoticeDtoTestFactory.createNoticeDtlDto();

        // When::
        NoticeEntity noticeEntity = noticeMapstruct.toEntity(noticeDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(noticeEntity);
    }




}