package io.nicheblog.dreamdiary.global.cmm.cd.model;

import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * DtlCdDtoTestFactory
 * <pre>
 *  상세코드 테스트 Dto 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class DtlCdDtoTestFactory {

    /**
     * 상세코드 상세 Dto 생성
     */
    public static DtlCdDto createDtlCdDtlDto() throws Exception {
        return DtlCdDto.builder()
                .clCd("test_cl_cd")
                .dtlCd("test_dtl_cd")
                .dtlCdNm("테스트_분류코드")
                .dc("설명")
                .build();
    }
}
