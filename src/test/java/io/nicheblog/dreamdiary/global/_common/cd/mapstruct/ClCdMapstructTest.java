package io.nicheblog.dreamdiary.global._common.cd.mapstruct;

import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global._common.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global._common.cd.entity.ClCdEntityTestFactory;
import io.nicheblog.dreamdiary.global._common.cd.model.ClCdDto;
import io.nicheblog.dreamdiary.global._common.cd.model.ClCdDtoTestFactory;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseEntityTestFactoryHelper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * ClCdMapstructTest
 * <pre>
 *  분류 코드 관리 Mapstruct 매핑 테스트 모듈.
 * </pre>
 * TODO: dtlCd 관련 체크 추가하기
 *
 * @author nichefish
 */
@ActiveProfiles("test")
@Log4j2
class ClCdMapstructTest {

    private final ClCdMapstruct clCdMapstruct = ClCdMapstruct.INSTANCE;

    /**
     * entity -> dto 검증 :: 기본 속성
     */
    @Test
    void testToDto_checkBasic() throws Exception {
        // Given::
        ClCdEntity clCdEntity = ClCdEntityTestFactory.createClCd();
        // 글분류 코드
        clCdEntity.setClCtgrCd(TestConstant.TEST_CL_CTGR_CD);

        // When::
        ClCdDto dto = clCdMapstruct.toDto(clCdEntity);

        // Then::
        assertNotNull(dto);
        assertEquals(dto.getClCd(), TestConstant.TEST_CL_CD);
        assertEquals(dto.getClCdNm(), TestConstant.TEST_CL_CD_NM);
        assertEquals(dto.getDc(), TestConstant.TEST_DC);
        // 글분류 코드
        assertEquals(dto.getClCtgrCd(), TestConstant.TEST_CL_CTGR_CD);
    }

    /* ----- */

    /**
     * entity -> listdto 검증 :: 기본 속성
     */
    @Test
    void testToListDto_checkBasic() throws Exception {
        // Given::
        ClCdEntity clCdEntity = ClCdEntityTestFactory.createClCd();
        // 글분류 코드
        clCdEntity.setClCtgrCd(TestConstant.TEST_CL_CTGR_CD);
        
        // When::
        ClCdDto dto = clCdMapstruct.toListDto(clCdEntity);

        // Then::
        assertNotNull(dto);
        assertEquals(dto.getClCd(), TestConstant.TEST_CL_CD);
        assertEquals(dto.getClCdNm(), TestConstant.TEST_CL_CD_NM);
        assertEquals(dto.getDc(), TestConstant.TEST_DC);
        // 글분류 코드
        assertEquals(dto.getClCtgrCd(), TestConstant.TEST_CL_CTGR_CD);
    }

    /* ----- */

    /**
     * dto -> entity 변환 검증 :: 기본 체크
     */
    @Test
    void testToEntity_checkBasic() throws Exception {
        // Given::
        ClCdDto clCdDto = ClCdDtoTestFactory.createClCdDtlDto();
        clCdDto.setClCtgrCd(TestConstant.TEST_CL_CTGR_CD);

        // When::
        ClCdEntity entity = clCdMapstruct.toEntity(clCdDto);

        // Then::
        assertNotNull(entity);
        // 글분류 코드
        assertEquals(entity.getClCtgrCd(), TestConstant.TEST_CL_CTGR_CD);
    }

    /**
     * dto -> entity 변환 검증 :: 등록자/수정자 정보
     */
    @Test
    void testToEntity_checkAuditor() throws Exception {
        // Given::
        ClCdEntity clCdEntity = ClCdEntityTestFactory.createClCd();
        // 등록자
        BaseEntityTestFactoryHelper.setRegstrInfo(clCdEntity);
        // 수정자
        BaseEntityTestFactoryHelper.setMdfusrInfo(clCdEntity);

        // When::
        ClCdDto dto = clCdMapstruct.toDto(clCdEntity);

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
     * update entity from dto 검증
     */
    @Test
    void testUpdateFromDto_checkBasic() throws Exception {
        // Given::
        ClCdEntity entity = ClCdEntityTestFactory.createClCd();
        ClCdDto dto = ClCdDtoTestFactory.createClCdDtlDto_1();
        dto.setClCtgrCd(TestConstant.TEST_CL_CTGR_CD_1);

        // When::
        clCdMapstruct.updateFromDto(dto, entity);

        // Then::
        assertNotNull(dto);
        assertEquals(dto.getClCd(), TestConstant.TEST_CL_CD_1);
        assertEquals(dto.getClCdNm(), TestConstant.TEST_CL_CD_NM_1);
        assertEquals(dto.getDc(), TestConstant.TEST_DC_1);
        // 글분류 코드
        assertEquals(dto.getClCtgrCd(), TestConstant.TEST_CL_CTGR_CD_1);
    }
}