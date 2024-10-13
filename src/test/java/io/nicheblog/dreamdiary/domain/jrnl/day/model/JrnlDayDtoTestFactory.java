package io.nicheblog.dreamdiary.domain.jrnl.day.model;

import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * JrnlDayDtoTestFactory
 * <pre>
 *  저널 일자 테스트 Dto 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class JrnlDayDtoTestFactory {

    /**
     * 테스트용 저널 일자 Dto 생성
     */
    public static JrnlDayDto createJrnlDayDtlDto() throws Exception {
        return JrnlDayDto.builder()
                .postNo(0)
                .contentType(ContentType.NOTICE.key)
                .build();
    }
}
