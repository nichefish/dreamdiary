package io.nicheblog.dreamdiary.domain.vcatn.schdul.entity;

import io.nicheblog.dreamdiary.domain.vcatn.papr.entity.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * VcatnSchdulEntityTestFactory
 * <pre>
 *  휴가 일정 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class VcatnSchdulEntityTestFactory {

    /**
     * 테스트용 휴가 일정 Entity 생성
     */
    public static VcatnSchdulEntity createVcatnSchdul() throws Exception {
        return VcatnSchdulEntity.builder()
                .bgnDt(DateUtils.asDate("2000-01-01"))
                .endDt(DateUtils.asDate("2000-01-02"))
                .vcatnCd(Constant.VCATN_ANNUAL)
                .resn("vcatn_resn")
                .build();
    }
}
