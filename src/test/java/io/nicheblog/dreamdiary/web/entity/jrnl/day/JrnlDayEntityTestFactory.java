package io.nicheblog.dreamdiary.web.entity.jrnl.day;

import io.nicheblog.dreamdiary.global.ContentType;
import lombok.experimental.UtilityClass;

/**
 * JrnlDayEntityTestFactory
 * <pre>
 *  저널 일자 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class JrnlDayEntityTestFactory {

    /**
     * 저널 일자 Entity 생성
     */
    public static JrnlDayEntity createJrnlDay() throws Exception {
        return JrnlDayEntity.builder()
                .postNo(0)
                .contentType(ContentType.JRNL_DAY.key)
                .build();
    }
}
