package io.nicheblog.dreamdiary.web.model.jrnl.day;

import io.nicheblog.dreamdiary.global.ContentType;
import lombok.experimental.UtilityClass;

/**
 * JrnlDayDtoTestFactory
 * <pre>
 *  저널 일자 테스트 Dto 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class JrnlDayDtoTestFactory {

    /**
     * 저널 일자 Dto 생성
     */
    public static JrnlDayDto.DTL createJrnlDayDtlDto() throws Exception {
        return JrnlDayDto.DTL.builder()
                .postNo(0)
                .contentType(ContentType.NOTICE.key)
                .jrnlDt("2000-01-01")
                .build();
    }
}
