package io.nicheblog.dreamdiary.web.mapstruct.jrnl.day;

import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntityTestFactory;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDto;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDtoTestFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JrnlDayMapstructTest
 * <pre>
 *  저널 일자 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
class JrnlDayMapstructTest {

    private final JrnlDayMapstruct jrnlDayMapstruct = JrnlDayMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        JrnlDayDto.DTL jrnlDayDto = JrnlDayDtoTestFactory.createJrnlDayDtlDto();

        // When::
        JrnlDayEntity jrnlDayEntity = jrnlDayMapstruct.toEntity(jrnlDayDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDayEntity);
        assertEquals(jrnlDayEntity.getJrnlDt(), DateUtils.asDate(jrnlDayEntity.getJrnlDt()));
    }

    /* ----- */

    /**
     * toDto 검증
     */
    @Test
    void toDto_checkBasic() throws Exception {
        // Given::
        JrnlDayEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDay();

        // When::
        JrnlDayDto.DTL jrnlDayDto = jrnlDayMapstruct.toDto(jrnlDayEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDayEntity);
        assertEquals(jrnlDayDto.getJrnlDt(), DateUtils.asStr(jrnlDayEntity.getJrnlDt(), DatePtn.DATE));
    }

}