package io.nicheblog.dreamdiary.global._common._clsf.viewer.entity;

import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * ViewerEntityTestFactory
 * <pre>
 *  열람자 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class ViewerEntityTestFactory {

    /**
     * 테스트용 열람자 Entity 생성
     */
    public static ViewerEntity create() throws Exception {
        return ViewerEntity.builder()
                .build();
    }
}
