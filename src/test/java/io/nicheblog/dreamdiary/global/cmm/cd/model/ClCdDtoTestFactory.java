package io.nicheblog.dreamdiary.global.cmm.cd.model;

import io.nicheblog.dreamdiary.global.TestConstant;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * ClCdDtoTestFactory
 * <pre>
 *  분류코드 테스트 Dto 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class ClCdDtoTestFactory {

    /**
     * 분류코드 상세 Dto 생성
     */
    public static ClCdDto createClCdDtlDto() throws Exception {
        return ClCdDto.builder()
                .clCd(TestConstant.TEST_CL_CD)
                .clCdNm(TestConstant.TEST_CL_CD_NM)
                .dc(TestConstant.TEST_DC)
                .build();
    }
}
