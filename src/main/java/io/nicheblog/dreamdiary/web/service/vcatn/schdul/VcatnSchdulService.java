package io.nicheblog.dreamdiary.web.service.vcatn.schdul;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.schdul.VcatnSchdulMapstruct;
import io.nicheblog.dreamdiary.web.model.vcatn.schdul.VcatnSchdulDto;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.web.repository.vcatn.jpa.VcatnSchdulRepository;
import io.nicheblog.dreamdiary.web.spec.vcatn.VcatnSchdulSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VcatnPaprService
 * <pre>
 *  휴가계획서 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("vcatnSchdulService")
@RequiredArgsConstructor
@Log4j2
public class VcatnSchdulService
        implements BaseCrudService<VcatnSchdulDto, VcatnSchdulDto, Integer, VcatnSchdulEntity, VcatnSchdulRepository, VcatnSchdulSpec, VcatnSchdulMapstruct> {

    private final VcatnSchdulRepository vcatnSchdulRepository;
    private final VcatnSchdulSpec vcatnSchdulSpec;
    private final VcatnSchdulMapstruct vcatnSchdulMapstruct = VcatnSchdulMapstruct.INSTANCE;

    @Override
    public VcatnSchdulRepository getRepository() {
        return this.vcatnSchdulRepository;
    }

    @Override
    public VcatnSchdulSpec getSpec() {
        return this.vcatnSchdulSpec;
    }

    @Override
    public VcatnSchdulMapstruct getMapstruct() {
        return this.vcatnSchdulMapstruct;
    }

    public List<VcatnSchdulDto> getListDto(VcatnStatsYyDto statsYy) throws Exception {
        Map<String, Object> searchParamMap = new HashMap(){{
            put("statsYy", statsYy.getStatsYy());
            put("statsYy", statsYy.getStatsYy());
        }};
        return this.getListDto(searchParamMap);
    }

    //
/**
 * 휴가관리 > 휴가사용일자 목록 Page<Entity>->Page<Dto> 변환
 *//*

    public Page<VcatnSchdulDto> pageEntityToDto(
            final Page<VcatnSchdulEntity> entityPage,
            final Map<String, Object> searchParamMap
    ) throws Exception {
        Date statsYyBgnDt = DateUtils.asDate(searchParamMap.get("searchStartDt"));
        Date statsYyEndDt = DateUtils.asDate(searchParamMap.get("searchEndDt"));
        List<VcatnSchdulDto> rsDyDtoList = new ArrayList<>();
        for (VcatnSchdulEntity vcatn : entityPage.getContent()) {
            // 연차(반차)만 계산한다. 공가, 무급/생리휴가, 경조휴가 등은 패스
            String vcatnTy = vcatn.getVcatnCd();
            // 반차는 0.5일, 연차는 1일 소진
            boolean isHalf = (Constant.VCATN_AM_HALF.equals(vcatnTy) || Constant.VCATN_PM_HALF.equals(vcatnTy));
            double exhrDy = isHalf ? 0.5 : Constant.VCATN_ANNUAL.equals(vcatnTy) ? 1 : 0;

            // 휴가 시작일자/종료일자 산정 : 2월 28일 전- 또는 3월 1일 후 식으로 경계에 걸쳐있는 경우를 따진다. (딱 경계까지만 일수 계산하기)
            Date vcatnBgnDt = vcatn.getBgnDt();
            Date vcatnEndDt = vcatn.getEndDt();
            if (vcatnBgnDt.compareTo(statsYyBgnDt) < 0) vcatnBgnDt = statsYyBgnDt;
            vcatnEndDt = (vcatnEndDt == null) ? vcatnBgnDt : (vcatnEndDt.compareTo(statsYyEndDt) > 0) ? statsYyEndDt : vcatnEndDt;

            // 날짜 훑으면서 각 일자별로 쪼갬. 공휴일 또는 주말여부 체크
            Date keyDt = vcatn.getBgnDt();
            while (keyDt.compareTo(vcatnEndDt) <= 0) {
                if (schdulService.isHldyOrWeekend(keyDt)) {
                    keyDt = DateUtils.getDateAddDay(keyDt, 1);
                    continue;
                }
                VcatnSchdulDto vcatnSchdul = vcatnSchdulMapstruct.toDyDto(vcatn);
                // 첫째날에는 특별히 수정 버튼 등 이것저것 달아줌
                if (keyDt.compareTo(vcatnBgnDt) == 0) {
                    vcatnSchdul.setVcatnSchdulNo(Integer.toString(vcatn.getVcatnSchdulNo()));
                    vcatnSchdul.setRm(vcatn.getRm());
                }
                vcatnSchdul.setVcatnSchdul(DateUtils.asStr(keyDt, DatePtn.DATE));
                vcatnSchdul.setVcatnExprDy(exhrDy);
                rsDyDtoList.add(vcatnSchdul);
                keyDt = DateUtils.getDateAddDay(keyDt, 1);
            }
        }
        rsDyDtoList.sort(Comparator.naturalOrder());
        return new PageImpl<>(rsDyDtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

}*/

}
