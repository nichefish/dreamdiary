package io.nicheblog.dreamdiary.web.mapstruct.dream.day;

import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import io.nicheblog.dreamdiary.web.entity.dream.day.DreamDayEntityTestFactory;
import io.nicheblog.dreamdiary.web.mapstruct.dream.DreamDayMapstruct;
import io.nicheblog.dreamdiary.web.model.dream.day.DreamDayDto;
import io.nicheblog.dreamdiary.web.model.dream.day.DreamDayDtoTestFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * DreamDayMapstructTest
 * <pre>
 *  꿈 일자 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
class DreamDayMapstructTest {

    private final DreamDayMapstruct dreamDayMapstruct = DreamDayMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        DreamDayDto.DTL dreamDayDto = DreamDayDtoTestFactory.createDreamDayDtlDto();

        // When::
        DreamDayEntity dreamDayEntity = dreamDayMapstruct.toEntity(dreamDayDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dreamDayEntity);
        assertEquals(dreamDayEntity.getDreamtDt(), DateUtils.asDate(dreamDayEntity.getDreamtDt()));
    }

    /* ----- */

    /**
     * toDto 검증
     */
    @Test
    void toDto_checkBasic() throws Exception {
        // Given::
        DreamDayEntity dreamDayEntity = DreamDayEntityTestFactory.createDreamDay();

        // When::
        DreamDayDto.DTL dreamDayDto = dreamDayMapstruct.toDto(dreamDayEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dreamDayEntity);
        assertEquals(dreamDayDto.getDreamtDt(), DateUtils.asStr(dreamDayEntity.getDreamtDt(), DatePtn.DATE));
    }

}