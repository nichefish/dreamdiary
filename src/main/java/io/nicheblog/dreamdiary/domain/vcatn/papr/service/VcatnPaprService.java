package io.nicheblog.dreamdiary.domain.vcatn.papr.service;

import io.nicheblog.dreamdiary.domain.vcatn.papr.entity.VcatnPaprEntity;
import io.nicheblog.dreamdiary.domain.vcatn.papr.entity.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.domain.vcatn.papr.mapstruct.VcatnPaprMapstruct;
import io.nicheblog.dreamdiary.domain.vcatn.papr.model.VcatnPaprDto;
import io.nicheblog.dreamdiary.domain.vcatn.papr.model.VcatnSchdulDto;
import io.nicheblog.dreamdiary.domain.vcatn.papr.repository.jpa.VcatnPaprRepository;
import io.nicheblog.dreamdiary.domain.vcatn.papr.spec.VcatnPaprSpec;
import io.nicheblog.dreamdiary.domain.vcatn.stats.model.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.domain.vcatn.stats.service.VcatnStatsYyService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global._common.cd.service.DtlCdService;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * VcatnPaprService
 * <pre>
 *  휴가계획서 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("vcatnPaprService")
@RequiredArgsConstructor
@Log4j2
public class VcatnPaprService
        implements BasePostService<VcatnPaprDto.DTL, VcatnPaprDto.LIST, Integer, VcatnPaprEntity, VcatnPaprRepository, VcatnPaprSpec, VcatnPaprMapstruct> {

    @Getter
    private final VcatnPaprRepository repository;
    @Getter
    private final VcatnPaprSpec spec;
    @Getter
    private final VcatnPaprMapstruct mapstruct = VcatnPaprMapstruct.INSTANCE;

    private final VcatnSchdulService vcatnSchdulService;
    private final VcatnStatsYyService vcatnStatsYyService;
    private final DtlCdService dtlCdService;

    /**
     * 등록 전처리. (override)
     * 
     * @param vcatnPaprDto 등록할 객체
     */
    @Override
    public void preRegist(final VcatnPaprDto.DTL vcatnPaprDto) {
        // 제목 자동 처리
        vcatnPaprDto.setTitle(this.initTitle(vcatnPaprDto));
    }

    /**
     * 수정 전처리. (override)
     *
     * @param vcatnPaprDto 수정할 객체
     */
    @Override
    public void preModify(final VcatnPaprDto.DTL vcatnPaprDto) {
        // 제목 자동 처리
        vcatnPaprDto.setTitle(this.initTitle(vcatnPaprDto));
    }

    /**
     * 제목 자동 처리.
     * 
     * @param vcatnPaprDto 처리할 객체
     */
    public String initTitle(final VcatnPaprDto vcatnPaprDto) {
        List<VcatnSchdulDto> schdulList = vcatnPaprDto.getSchdulList();
        if (CollectionUtils.isEmpty(schdulList)) return "휴가계획서 (날짜없음)";
        return schdulList.stream()
                .map(vcatn -> {
                    String vcatnCd = vcatn.getVcatnCd();
                    String vcatnNm = dtlCdService.getDtlCdNm(Constant.VCATN_CD, vcatnCd);
                    String period = vcatn.getBgnDt();
                    String endDt = vcatn.getEndDt();
                    try {
                        boolean isSameDay = DateUtils.isSameDay(period, endDt) || (endDt == null);
                        if (!isSameDay) {
                            period += "~" + endDt;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return "(" + vcatnNm + ")" + period;
                })
                .collect(Collectors.joining(", "));
    }

    /**
     * 일정 > 휴가계획서 > 휴가계획서 상세보기 > 확인 여부 변경
     */
    public VcatnPaprDto cf(final Integer key) throws Exception {
        VcatnPaprEntity vcatnPaprEntity = this.getDtlEntity(key);
        vcatnPaprEntity.setCfYn("Y");
        // update
        VcatnPaprEntity rsltEntity = this.updt(vcatnPaprEntity);
        VcatnPaprDto rsltDto = mapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess(rsltEntity.getPostNo() != null);
        return rsltDto;
    }

    /**
     * 삭제 전처리. (override)
     *
     * @param entity - 삭제할 엔티티
     */
    @Override
    public void preDelete(final VcatnPaprEntity entity) {
        // 휴가계획서 상세항목 삭제
        List<VcatnSchdulEntity> vcatnSchdulList = entity.getSchdulList();
        vcatnSchdulService.deleteAll(vcatnSchdulList);
    }

    /**
     * 휴가계획서 중 가장 오래된 등록 년도부터 현재 년도까지의 년도 목록을 생성하여 반환합니다.
     *
     * @return {@link List} -- 휴가계획서 년도 목록
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public List<VcatnStatsYyDto> getVcatnYyList() throws Exception {
        String minYyStr = repository.selectMinYy();
        int minYy = (minYyStr != null) ? Integer.parseInt(minYyStr) : DateUtils.getCurrYy();
        List<VcatnStatsYyDto> yyList = new ArrayList<>();
        int currYy = DateUtils.getCurrYy();
        for (int yy = minYy; yy <= currYy; yy++) {     // 최저년도~현재년도. 현재년도는 항상 들어가도록
            yyList.add(vcatnStatsYyService.getVcatnYyDtDto(Integer.toString(yy)));
        }
        return yyList;
    }
}
