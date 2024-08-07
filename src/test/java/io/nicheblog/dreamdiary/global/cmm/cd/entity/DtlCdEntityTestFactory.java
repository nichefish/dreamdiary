package io.nicheblog.dreamdiary.global.cmm.cd.entity;

import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * DtlCdEntityTestFactory
 * <pre>
 *  상세코드 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class DtlCdEntityTestFactory {

    /**
     * 상세코드 Entity 생성
     */
    public static DtlCdEntity createDtlCd() throws Exception {
        return DtlCdEntity.builder()
                .clCd("test_cl_cd")
                .dtlCd("test_dtl_cd")
                .dtlCdNm("테스트_분류코드")
                .dc("설명")
                .build();
    }
}
