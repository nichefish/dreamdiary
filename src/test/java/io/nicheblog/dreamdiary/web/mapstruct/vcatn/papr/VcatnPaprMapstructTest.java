package io.nicheblog.dreamdiary.web.mapstruct.vcatn.papr;

import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnPaprEntity;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprDtoTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * VcatnPaprMapstructTest
 * <pre>
 *  휴가계획서 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
class VcatnPaprMapstructTest {

    private final VcatnPaprMapstruct vcatnPaprMapstruct = VcatnPaprMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        VcatnPaprDto.DTL vcatnPaprDto = VcatnPaprDtoTestFactory.createVcatnPaprDtlDto();

        // When::
        VcatnPaprEntity vcatnPaprEntity = vcatnPaprMapstruct.toEntity(vcatnPaprDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(vcatnPaprEntity);
    }
}