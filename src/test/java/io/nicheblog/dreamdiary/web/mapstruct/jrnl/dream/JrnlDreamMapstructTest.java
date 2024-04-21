package io.nicheblog.dreamdiary.web.mapstruct.jrnl.dream;

import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.BaseEntityTestFactoryHelper;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntityTestFactory;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntityTestFactory;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
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
     * toDto 검증
     */
    @Test
    void toDto_checkBasic() throws Exception {
        // Given::
        JrnlDayEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDay();
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
     * toDto 검증 :: 등록자/수정자 정보 체크
     */
    @Test
    void testToDto_checkAuditor() throws Exception {
        // Given::
        JrnlDreamEntity jrnlDreamEntity = JrnlDreamEntityTestFactory.createJrnlDream();        JrnlDayEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDay();
        jrnlDayEntity.setJrnlDt(DateUtils.asDate("2000-01-01"));
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
}