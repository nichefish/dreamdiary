package io.nicheblog.dreamdiary.domain.jrnl.diary.entity;

import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * JrnlDiaryEntityTestFactory
 * <pre>
 *  저널 꿈 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class JrnlDiaryEntityTestFactory {

    /**
     * 테스트용 저널 꿈 Entity 생성
     */
    public static JrnlDiaryEntity createJrnlDiary() throws Exception {
        return JrnlDiaryEntity.builder()
                .postNo(0)
                .contentType(ContentType.JRNL_DIARY.key)
                .title("test_title")
                .cn("test_cn")
                .ctgrCd("test_ctgr_cd")
                .build();
    }
}
