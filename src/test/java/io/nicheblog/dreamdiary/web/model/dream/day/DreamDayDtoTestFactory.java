package io.nicheblog.dreamdiary.web.model.dream.day;

import io.nicheblog.dreamdiary.global.ContentType;
import lombok.experimental.UtilityClass;

/**
 * DreamDayDtoTestFactory
 * <pre>
 *  꿈 일자 테스트 Dto 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class DreamDayDtoTestFactory {

    /**
     * 꿈 일자 Dto 생성
     */
    public static DreamDayDto.DTL createDreamDayDtlDto() throws Exception {
        return DreamDayDto.DTL.builder()
                .postNo(0)
                .contentType(ContentType.NOTICE.key)
                .dreamtDt("2000-01-01")
                .build();
    }
}
