package io.nicheblog.dreamdiary.web.service.vcatn.stats;

import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.stats.VcatnStatsYyEntity;
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.stats.VcatnStatsYyMapstruct;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.web.repository.vcatn.VcatnStatsYyRepository;
import io.nicheblog.dreamdiary.web.spec.vcatn.VcatnStatsYySpec;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * VcatnStatsYyService
 * <pre>
 *  휴가 기준년도 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("vcatnStatsYyService")
@Log4j2
public class VcatnStatsYyService {

    @Resource(name = "vcatnStatsYySpec")
    private VcatnStatsYySpec vcatnStatsYySpec;

    @Resource(name = "vcatnStatsYyRepository")
    private VcatnStatsYyRepository vcatnStatsYyRepository;

    /**
     * 휴가관리 > 휴가 기준 년도 등록
     * 매년 1월 1일에 등록함 (0301~0228)
     */
    public Boolean regVcatnYyDt() throws Exception {
        // 매년 1월1일에 다음단위(0301~0228)것 등록
        String yyStr = DateUtils.getCurrYearStr();
        Date startDt = DateUtils.asDate(yyStr + "0301");
        String nextYyStr = Integer.toString(DateUtils.getCurrYear() + 1);
        Date endDt = DateUtils.getDateAddDay(DateUtils.asDate(nextYyStr + "0301"), -1);
        VcatnStatsYyEntity entity = new VcatnStatsYyEntity(yyStr, startDt, endDt);
        vcatnStatsYyRepository.save(entity);
        return true;
    }

    /**
     * 휴가관리 > 휴가 기준 년도 조회 (entity level)
     * DB에 입력된 값에 대해서 조회 :: 입력값이 없을 경우 일반적으로 1월 1일 ~ 12월 31일로 처리한다.
     */
    public VcatnStatsYyEntity getVcatnYyDtEntity(final String yyStr) throws Exception {
        Optional<VcatnStatsYyEntity> statsYyWrapper = vcatnStatsYyRepository.findById(yyStr);
        return statsYyWrapper.orElse(new VcatnStatsYyEntity(yyStr));
    }

    /**
     * 휴가관리 > 휴가 기준 년도 조회 (entity level)
     * DB에 입력된 값에 대해서 조회 :: 입력값이 없을 경우 일반적으로 1월 1일 ~ 12월 31일로 처리한다.
     */
    public VcatnStatsYyDto getVcatnYyDtDto(final String yyStr) throws Exception {
        if (StringUtils.isEmpty(yyStr)) return this.getCurrVcatnYyDt();
        return VcatnStatsYyMapstruct.INSTANCE.toDto(this.getVcatnYyDtEntity(yyStr));
    }

    /**
     * 휴가관리 > 휴가 기준 년도 시작/종료일자가 담긴 Map 반환
     */
    public Map<String, Object> getVcatnYyDtMap(final VcatnStatsYyDto statsYy) throws Exception {
        if (statsYy == null) return null;
        return new HashMap<>() {{
            put("searchStartDt", DateUtils.asDate(statsYy.getBgnDt()));
            put("searchEndDt", DateUtils.asDate(statsYy.getEndDt()));
        }};
    }

    /**
     * 휴가관리 > 오늘날짜가 속한 휴가 기준 정보 반환
     */
    public VcatnStatsYyDto getCurrVcatnYyDt() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("today", DateUtils.getCurrDate());
        }};
        Page<VcatnStatsYyEntity> statsYyPage = vcatnStatsYyRepository.findAll(vcatnStatsYySpec.searchWith(searchParamMap), Pageable.unpaged());
        List<VcatnStatsYyEntity> statsYyList = statsYyPage.getContent();
        if (CollectionUtils.isEmpty(statsYyList)) return null;
        VcatnStatsYyEntity statsYy = statsYyList.get(0);
        return VcatnStatsYyMapstruct.INSTANCE.toDto(statsYy);
    }
}
