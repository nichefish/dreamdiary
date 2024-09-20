package io.nicheblog.dreamdiary.web.mapstruct.notice;

import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.web.entity.BaseEntityTestFactoryHelper;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntityTestFactory;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDtoTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
     * toDto 검증 :: 등록자/수정자 정보
     */
    @Test
    void testToDto_checkAuditor() throws Exception {
        // Given::
        NoticeEntity noticeEntity = NoticeEntityTestFactory.createNotice();
        // 등록자
        BaseEntityTestFactoryHelper.setRegstrInfo(noticeEntity);
        // 수정자
        BaseEntityTestFactoryHelper.setMdfusrInfo(noticeEntity);

        // When::
        NoticeDto dto = noticeMapstruct.toDto(noticeEntity);

        // Then::
        assertNotNull(dto);
        // 등록자
        assertEquals(dto.getRegstrId(), TestConstant.TEST_REGSTR_ID);
        assertEquals(dto.getRegstrNm(), TestConstant.TEST_REGSTR_NM);
        assertEquals(dto.getRegDt(), "2000-01-01 00:00:00");
        // 수정자
        assertEquals(dto.getMdfusrId(), TestConstant.TEST_MDFUSR_ID);
        assertEquals(dto.getMdfusrNm(), TestConstant.TEST_MDFUSR_NM);
        assertEquals(dto.getMdfDt(), "2000-01-01 00:00:00");
    }

    /* ----- */

    /**
     * dto -> entity 검증
     */
    @Test
    void testToEntity_checkBasic() throws Exception {
        // Given::
        NoticeDto.DTL noticeDto = NoticeDtoTestFactory.createNoticeDtlDto();

        // When::
        NoticeEntity noticeEntity = noticeMapstruct.toEntity(noticeDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(noticeEntity);
    }

    /* ----- */

    /**
     * updateFromDto 검증 :: 기본 속성
     * TODO
     */
    @Test
    void testUpdateFromDto_checkBasic() throws Exception {
        //
    }
}