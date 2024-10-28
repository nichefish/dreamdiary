package io.nicheblog.dreamdiary.global._common.cd.entity;

import io.nicheblog.dreamdiary.global.TestConstant;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * ClCdEntityTestFactory
 * <pre>
 *  분류 코드 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class ClCdEntityTestFactory {

    /**
     * 테스트용 분류 코드 Entity 생성
     */
    public static ClCdEntity createClCd() throws Exception {
        return ClCdEntity.builder()
                .clCd(TestConstant.TEST_CL_CD)
                .clCdNm(TestConstant.TEST_CL_CD_NM)
                .dc(TestConstant.TEST_DC)
                .build();
    }
}
