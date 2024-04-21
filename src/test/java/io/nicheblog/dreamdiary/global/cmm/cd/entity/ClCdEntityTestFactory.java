package io.nicheblog.dreamdiary.global.cmm.cd.entity;

import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * ClCdEntityTestFactory
 * <pre>
 *  분류코드 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class ClCdEntityTestFactory {

    /**
     * 분류코드 Entity 생성
     */
    public static ClCdEntity createClCd() throws Exception {
        return ClCdEntity.builder()
                .clCd("test")
                .clCdNm("테스트_분류코드")
                .dc("설명")
                .build();
    }
}
