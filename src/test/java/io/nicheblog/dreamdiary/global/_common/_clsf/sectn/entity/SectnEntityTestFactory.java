package io.nicheblog.dreamdiary.global._common._clsf.sectn.entity;

import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * SectnEntityTestFactory
 * <pre>
 *  단락 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class SectnEntityTestFactory {

    /**
     * 테스트용 단락 Entity 생성
     */
    public static SectnEntity create() throws Exception {
        return SectnEntity.builder()
                .contentType(ContentType.SECTN.key)
                .build();
    }
}
