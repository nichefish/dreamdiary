package io.nicheblog.dreamdiary.domain.vcatn.papr.entity;

import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * VcatnPaprEntityTestFactory
 * <pre>
 *  휴가계획서 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class VcatnPaprEntityTestFactory {

    /**
     * 휴가계획서 Entity 생성
     */
    public static VcatnPaprEntity createVcatnPapr() throws Exception {
        return VcatnPaprEntity.builder()
                .build();
    }
}
