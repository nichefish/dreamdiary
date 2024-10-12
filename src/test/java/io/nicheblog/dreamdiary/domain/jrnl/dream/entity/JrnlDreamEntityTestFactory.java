package io.nicheblog.dreamdiary.domain.jrnl.dream.entity;

import io.nicheblog.dreamdiary.global.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * JrnlDreamEntityTestFactory
 * <pre>
 *  저널 꿈 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class JrnlDreamEntityTestFactory {

    /**
     * 테스트용 저널 꿈 Entity 생성
     */
    public static JrnlDreamEntity createJrnlDream() throws Exception {
        return JrnlDreamEntity.builder()
                .postNo(0)
                .contentType(ContentType.JRNL_DREAM.key)
                .title("test_title")
                .cn("test_cn")
                .ctgrCd("test_ctgr_cd")
                .build();
    }
}
