package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntityTestFactory;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCdDto;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCdDtoTestFactory;
import io.nicheblog.dreamdiary.web.entity.BaseEntityTestFactoryHelper;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * ClCdMapstructTest
 * <pre>
 *  분류코드 관리 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
class ClCdMapstructTest {

    private final ClCdMapstruct clCdMapstruct = ClCdMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        ClCdEntity clCdEntity = ClCdEntityTestFactory.createClCd();
        clCdEntity.setClCtgrCd("test_cl_ctgr_cd");

        // When::
        ClCdDto dto = clCdMapstruct.toDto(clCdEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
    }

    /**
     * toDto 검증
     */
    @Test
    void toDto_checkBasic() throws Exception {
        // Given::
        ClCdEntity clCdEntity = ClCdEntityTestFactory.createClCd();
        clCdEntity.setClCtgrCd("test_cl_ctgr_cd");

        // When::
        ClCdDto dto = clCdMapstruct.toDto(clCdEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
    }

    /**
     * toDto 검증 :: 기본 체크
     */
    @Test
    void testToEntity_checkBasic() throws Exception {
        // Given::
        ClCdDto clCdDto = ClCdDtoTestFactory.createClCdDtlDto();
        clCdDto.setClCtgrCd("test_cl_ctgr_cd");

        // When::
        ClCdEntity entity = clCdMapstruct.toEntity(clCdDto);

        // Then::
        assertNotNull(entity);
        assertNotNull(entity.getClCtgrCd());
    }

    /**
     * toDto 검증 :: 등록자/수정자 정보 체크
     */
    @Test
    void testToDto_checkAuditor() throws Exception {
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
}