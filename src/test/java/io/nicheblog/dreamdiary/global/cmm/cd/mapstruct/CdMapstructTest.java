package io.nicheblog.dreamdiary.global.cmm.cd.mapstruct;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntityTestFactory;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntityTestFactory;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCdDto;
import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCdDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * CdMapstructTest
 * <pre>
 *  코드 Mapstruct 매핑 테스트 모듈
 *  (단순 코드 조회용이므로 auditor 정보 매핑 상속받지 않음)
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
class CdMapstructTest {

    private final CdMapstruct cdMapstruct = CdMapstruct.INSTANCE;

    /**
     * toDto 검증 (분류코드)
     */
    @Test
    void testToDto_cl_checkBasic() throws Exception {
        // Given::
        ClCdEntity clCdEntity = ClCdEntityTestFactory.createClCd();

        // When::
        ClCdDto dto = cdMapstruct.toDto(clCdEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
    }

    /**
     * toDto 검증 (상세코드)
     */
    @Test
    void testToDto_dtl_checkBasic() throws Exception {
        // Given::
        DtlCdEntity dtlCdEntity = DtlCdEntityTestFactory.createDtlCd();

        // When::
        DtlCdDto dto = cdMapstruct.toDto(dtlCdEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
    }
}