package io.nicheblog.dreamdiary.domain.vcatn.schdul.model;

import io.nicheblog.dreamdiary.domain.vcatn.papr.model.VcatnSchdulDto;
import io.nicheblog.dreamdiary.global.Constant;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * VcatnSchdulDtoTestFactory
 * <pre>
 *  휴가 일정 테스트 Dto 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class VcatnSchdulDtoTestFactory {

    /**
     * 테스트용 휴가 일정 Dto 생성
     */
    public static VcatnSchdulDto createVcatnSchdulDtlDto() throws Exception {
        return VcatnSchdulDto.builder()
                .bgnDt("2000-01-01")
                .endDt("2000-01-02")
                .vcatnCd(Constant.VCATN_ANNUAL)
                .resn("vcatn_resn")
                .build();
    }
}
