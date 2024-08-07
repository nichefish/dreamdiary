package io.nicheblog.dreamdiary.web.mapstruct.jrnl.day;

import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.BaseEntityTestFactoryHelper;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntityTestFactory;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntityTestFactory;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntityTestFactory;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDto;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDtoTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

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
@ActiveProfiles("test")
class JrnlDayMapstructTest {

    private final JrnlDayMapstruct jrnlDayMapstruct = JrnlDayMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        JrnlDayDto jrnlDayDto = JrnlDayDtoTestFactory.createJrnlDayDtlDto();
        jrnlDayDto.setJrnlDt("2000-01-01");

        // When::
        JrnlDayEntity jrnlDayEntity = jrnlDayMapstruct.toEntity(jrnlDayDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDayEntity);
        assertEquals(jrnlDayEntity.getJrnlDt(), DateUtils.asDate(jrnlDayEntity.getJrnlDt()));
    }

    /**
     * toEntity 검증 :: 대락일자
     */
    @Test
    void toEntity_checkAprxmtDt() throws Exception {
        // Given::
        JrnlDayDto jrnlDayDto = JrnlDayDtoTestFactory.createJrnlDayDtlDto();
        // 대략일자 세팅
        jrnlDayDto.setDtUnknownYn("Y");
        jrnlDayDto.setAprxmtDt("2000-01-01");

        // When::
        JrnlDayEntity jrnlDayEntity = jrnlDayMapstruct.toEntity(jrnlDayDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDayEntity);
        // 대략일자 여부
        assertEquals(jrnlDayEntity.getDtUnknownYn(), jrnlDayDto.getDtUnknownYn());
        assertEquals(jrnlDayEntity.getAprxmtDt(), DateUtils.asDate(jrnlDayDto.getAprxmtDt()));
    }

    /* ----- */

    /**
     * toDto 검증
     */
    @Test
    void toDto_checkBasic() throws Exception {
        // Given::
        JrnlDayEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDay();
        jrnlDayEntity.setJrnlDt(DateUtils.asDate("2000-01-01"));

        // When::
        JrnlDayDto jrnlDayDto = jrnlDayMapstruct.toDto(jrnlDayEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDayEntity);
        assertEquals(jrnlDayDto.getJrnlDt(), DateUtils.asStr(jrnlDayEntity.getJrnlDt(), DatePtn.DATE));
        assertNotNull(jrnlDayDto.getJrnlDtWeekDay());
        assertEquals(jrnlDayDto.getJrnlDtWeekDay(), "土");
        assertEquals(jrnlDayDto.getStdrdDt(), jrnlDayDto.getJrnlDt());
    }

    /**
     * toDto 검증 :: 등록자/수정자 정보 체크
     */
    @Test
    void testToDto_checkAuditor() throws Exception {
        // Given::
        JrnlDayEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDay();
        // 등록자
        BaseEntityTestFactoryHelper.setRegstrInfo(jrnlDayEntity);
        // 수정자
        BaseEntityTestFactoryHelper.setMdfusrInfo(jrnlDayEntity);

        // When::
        JrnlDayDto dto = jrnlDayMapstruct.toDto(jrnlDayEntity);

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

    /**
     * toDto 검증 :: 대략일자
     */
    @Test
    void toDto_checkAprxmtDt() throws Exception {
        // Given::
        JrnlDayEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDay();
        // 대략일자 세팅
        jrnlDayEntity.setDtUnknownYn("Y");
        jrnlDayEntity.setAprxmtDt(DateUtils.asDate("2000-01-01"));

        // When::
        JrnlDayDto jrnlDayDto = jrnlDayMapstruct.toDto(jrnlDayEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDayEntity);
        assertEquals(jrnlDayDto.getDtUnknownYn(), jrnlDayEntity.getDtUnknownYn());
        assertEquals(jrnlDayDto.getAprxmtDt(), DateUtils.asStr(jrnlDayEntity.getAprxmtDt(), DatePtn.DATE));
        assertEquals(jrnlDayDto.getStdrdDt(), jrnlDayDto.getAprxmtDt());
    }

    /**
     * toDto 검증 :: dreamList
     */
    @Test
    void toDto_checkDreamList() throws Exception {
        // Given::
        JrnlDayEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDay();
        JrnlDreamEntity aa = JrnlDreamEntityTestFactory.createJrnlDream();
        JrnlDreamEntity bb = JrnlDreamEntityTestFactory.createJrnlDream();
        jrnlDayEntity.setJrnlDreamList(List.of(aa, bb));

        // When::
        JrnlDayDto jrnlDayDto = jrnlDayMapstruct.toDto(jrnlDayEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDayDto);
        assertNotNull(jrnlDayDto.getJrnlDreamList());
    }

    /**
     * toDto 검증 :: diaryList
     */
    @Test
    void toDto_checkDiaryList() throws Exception {
        // Given::
        JrnlDayEntity jrnlDayEntity = JrnlDayEntityTestFactory.createJrnlDay();
        JrnlDiaryEntity aa = JrnlDiaryEntityTestFactory.createJrnlDiary();
        JrnlDiaryEntity bb = JrnlDiaryEntityTestFactory.createJrnlDiary();
        jrnlDayEntity.setJrnlDiaryList(List.of(aa, bb));

        // When::
        JrnlDayDto jrnlDayDto = jrnlDayMapstruct.toDto(jrnlDayEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(jrnlDayDto);
        assertNotNull(jrnlDayDto.getJrnlDiaryList());
    }
}