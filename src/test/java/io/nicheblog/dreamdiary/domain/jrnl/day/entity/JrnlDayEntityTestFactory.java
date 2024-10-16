package io.nicheblog.dreamdiary.domain.jrnl.day.entity;

import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * JrnlDayEntityTestFactory
 * <pre>
 *  저널 일자 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class JrnlDayEntityTestFactory {

    /**
     * 테스트용 저널 일자 Entity 생성
     */
    public static JrnlDayEntity createJrnlDay() throws Exception {
        return JrnlDayEntity.builder()
                .postNo(0)
                .contentType(ContentType.JRNL_DAY.key)
                .build();
    }

    /**
     * 테스트용 저널 일자 Entity (simple) 생성
     */
    public static JrnlDaySmpEntity createJrnlDaySmp() throws Exception {
        return JrnlDaySmpEntity.builder()
                .postNo(0)
                .contentType(ContentType.JRNL_DAY.key)
                .build();
    }
}
