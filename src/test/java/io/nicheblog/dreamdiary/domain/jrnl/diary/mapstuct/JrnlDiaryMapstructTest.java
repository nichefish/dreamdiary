package io.nicheblog.dreamdiary.domain.jrnl.diary.mapstuct;

import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDayEntityTestFactory;
import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDaySmpEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryEntityTestFactory;
import io.nicheblog.dreamdiary.domain.jrnl.diary.mapstruct.JrnlDiaryMapstruct;
import io.nicheblog.dreamdiary.domain.jrnl.diary.model.JrnlDiaryDto;
import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseEntityTestFactoryHelper;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JrnlDiaryMapstructTest
 * <pre>
 *  저널 일기 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
class JrnlDiaryMapstructTest {

    private final JrnlDiaryMapstruct jrnlDiaryMapstruct = JrnlDiaryMapstruct.INSTANCE;
    
    /**
     * entity -> dto 검증
     */
    @Test
    void testToDto_checkBasic() throws Exception {
        // Given::
        JrnlDaySmpEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDaySmp();
        jrnlDayEntity.setJrnlDt(DateUtils.asDate("2000-01-01"));

        JrnlDiaryEntity jrnlDiaryEntity = JrnlDiaryEntityTestFactory.createJrnlDiary();
        jrnlDiaryEntity.setJrnlDay(jrnlDayEntity);

        // When::
        JrnlDiaryDto jrnlDiaryDto = jrnlDiaryMapstruct.toDto(jrnlDiaryEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDiaryDto);
        assertEquals(jrnlDiaryDto.getStdrdDt(), DateUtils.asStr(jrnlDiaryEntity.getJrnlDay().getJrnlDt(), DatePtn.DATE));
    }

    /**
     * entity -> dto 검증 :: 등록자/수정자 정보
     */
    @Test
    void testToDto_checkAuditor() throws Exception {
        // Given::
        JrnlDiaryEntity jrnlDiaryEntity = JrnlDiaryEntityTestFactory.createJrnlDiary();
        JrnlDaySmpEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDaySmp();
        jrnlDayEntity.setJrnlDt(DateUtils.asDate("2000-01-01"));
        jrnlDiaryEntity.setJrnlDay(jrnlDayEntity);
        // 등록자
        BaseEntityTestFactoryHelper.setRegstrInfo(jrnlDiaryEntity);
        // 수정자
        BaseEntityTestFactoryHelper.setMdfusrInfo(jrnlDiaryEntity);

        // When::
        JrnlDiaryDto dto = jrnlDiaryMapstruct.toDto(jrnlDiaryEntity);

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
     * updateFromDto 검증 :: 기본 속성
     * TODO
     */
    @Test
    void testUpdateFromDto_checkBasic() throws Exception {
        //
    }
}