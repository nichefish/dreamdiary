package io.nicheblog.dreamdiary.web.mapstruct.jrnl.diary;

import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.BaseEntityTestFactoryHelper;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntityTestFactory;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDaySmpEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntityTestFactory;
import io.nicheblog.dreamdiary.web.model.jrnl.diary.JrnlDiaryDto;
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
     * toDto 검증
     */
    @Test
    void toDto_checkBasic() throws Exception {
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
     * toDto 검증 :: 등록자/수정자 정보 체크
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
}