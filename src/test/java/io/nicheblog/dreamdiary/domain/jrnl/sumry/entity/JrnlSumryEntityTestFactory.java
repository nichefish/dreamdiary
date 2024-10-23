package io.nicheblog.dreamdiary.domain.jrnl.sumry.entity;

import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * JrnlSumryEntityTestFactory
 * <pre>
 *  저널 결산 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class JrnlSumryEntityTestFactory {

    /**
     * 테스트용 저널 결산 Entity 생성
     */
    public static JrnlSumryEntity create() throws Exception {
        return JrnlSumryEntity.builder()
                .contentType(ContentType.JRNL_SUMRY.key)
                .title("test_title")
                .cn("test_cn")
                .ctgrCd("test_ctgr_cd")
                .build();
    }
}
