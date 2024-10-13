package io.nicheblog.dreamdiary.domain.jrnl.dream.mapstuct;

import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDayEntityTestFactory;
import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDaySmpEntity;
import io.nicheblog.dreamdiary.domain.jrnl.dream.entity.JrnlDreamEntity;
import io.nicheblog.dreamdiary.domain.jrnl.dream.entity.JrnlDreamEntityTestFactory;
import io.nicheblog.dreamdiary.domain.jrnl.dream.mapstruct.JrnlDreamMapstruct;
import io.nicheblog.dreamdiary.domain.jrnl.dream.model.JrnlDreamDto;
import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseEntityTestFactoryHelper;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JrnlDreamMapstructTest
 * <pre>
 *  저널 꿈 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
class JrnlDreamMapstructTest {

    private final JrnlDreamMapstruct jrnlDreamMapstruct = JrnlDreamMapstruct.INSTANCE;

    /**
     * entity -> dto 검증
     */
    @Test
    void testToDto_checkBasic() throws Exception {
        // Given::
        JrnlDaySmpEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDaySmp();
        jrnlDayEntity.setJrnlDt(DateUtils.asDate("2000-01-01"));

        JrnlDreamEntity jrnlDreamEntity = JrnlDreamEntityTestFactory.createJrnlDream();
        jrnlDreamEntity.setJrnlDay(jrnlDayEntity);

        // When::
        JrnlDreamDto jrnlDreamDto = jrnlDreamMapstruct.toDto(jrnlDreamEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDreamDto);
        assertEquals(jrnlDreamDto.getStdrdDt(), DateUtils.asStr(jrnlDreamEntity.getJrnlDay().getJrnlDt(), DatePtn.DATE));
    }

    /**
     * entity -> dto 검증 :: 등록자/수정자 정보
     */
    @Test
    void testToDto_checkAuditor() throws Exception {
        // Given::
        JrnlDaySmpEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDaySmp();
        jrnlDayEntity.setJrnlDt(DateUtils.asDate("2000-01-01"));
        JrnlDreamEntity jrnlDreamEntity = JrnlDreamEntityTestFactory.createJrnlDream();
        jrnlDreamEntity.setJrnlDay(jrnlDayEntity);
        // 등록자
        BaseEntityTestFactoryHelper.setRegstrInfo(jrnlDreamEntity);
        // 수정자
        BaseEntityTestFactoryHelper.setMdfusrInfo(jrnlDreamEntity);

        // When::
        JrnlDreamDto dto = jrnlDreamMapstruct.toDto(jrnlDreamEntity);

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