package io.nicheblog.dreamdiary.web.entity.dream.day;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import lombok.experimental.UtilityClass;

/**
 * DreamDayEntityTestFactory
 * <pre>
 *  꿈 일자 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class DreamDayEntityTestFactory {

    /**
     * 꿈 일자 Entity 생성
     */
    public static DreamDayEntity createDreamDay() throws Exception {
        return DreamDayEntity.builder()
                .postNo(0)
                .contentType(ContentType.NOTICE.key)
                .dreamtDt(DateUtils.asDate("2000-01-01"))
                .build();
    }
}
