package io.nicheblog.dreamdiary.api.kasi.service;

import io.nicheblog.dreamdiary.api.kasi.mapstruct.HldyKasiApiMapstruct;
import io.nicheblog.dreamdiary.api.kasi.model.HldyKasiApiItemDto;
import io.nicheblog.dreamdiary.api.kasi.model.HldyKasiApiRespDto;
import io.nicheblog.dreamdiary.global.util.DateParser;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulEntity;
import io.nicheblog.dreamdiary.web.repository.schdul.SchdulRepository;
import io.nicheblog.dreamdiary.web.service.schdul.SchdulService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HldyKasiApiService
 * <pre>
 *  API:: 한국천문연구원(KASI):: 휴일 정보 조회 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("hldyKasiApiService")
@Log4j2
public class HldyKasiApiService {

    private final HldyKasiApiMapstruct hldyApiMapstruct = HldyKasiApiMapstruct.INSTANCE;

    @Value("${api.kasi.serviceKey}")
    private final String serviceKey = "nEV3ii2%2B%2BQx%2FlOcC4W9PFXRDzAEeaL9saxCefl7bLKs6i1DIa9B1joBwZZfSj8V5whJnvwYyWMc7l7hL%2F9bqQA%3D%3D";

    @Resource(name = "schdulService")
    private SchdulService schdulService;

    @Resource(name = "schdulRepository")
    private SchdulRepository schdulRepository;

    /**
     * API:: 한국천문연구원(KASI):: 휴일 정보 조회
     */
    public List<HldyKasiApiItemDto> getHldyList(final String yyParam) throws Exception {
        String yyStr = !StringUtils.isEmpty(yyParam) ? yyParam : DateUtils.getCurrYearStr();
        RestTemplate restTemplate = new RestTemplate();
        List<HldyKasiApiItemDto> rsItems = new ArrayList<>();
        // URL 설정
        final String holidayURL = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
        int numOfRows = 30;
        try {
            URI requestURI = new URI(holidayURL + "?solYear=" + yyStr + "&numOfRows=" + numOfRows + "&ServiceKey=" + serviceKey);
            HldyKasiApiRespDto respDto = restTemplate.getForObject(requestURI, HldyKasiApiRespDto.class);
            if (respDto != null) rsItems = respDto.getBody().getItems();
        } catch (Exception e) {
            MessageUtils.getExceptionMsg(e);
        }
        return rsItems;
    }

    /**
     * API:: 한국천문연구원(KASI):: 휴일 정보 받아와서 DB 저장
     * Clears Cache : hldyEntityList, isHldy, isHldyOrWeekend
     */
    @CacheEvict(value = {"hldyEntityList", "isHldy", "isHldyOrWeekend"}, allEntries = true)
    public Boolean regHldyList(final List<HldyKasiApiItemDto> hldyApiList) throws Exception {
        // dto to entity
        List<SchdulEntity> schdulList = new ArrayList<>();
        for (HldyKasiApiItemDto hldy : hldyApiList) {
            schdulList.add(hldyApiMapstruct.toEntity(hldy));
        }
        schdulRepository.saveAll(schdulList);

        return true;
    }

    /**
     * API:: 한국천문연구원(KASI):: API 조회 휴일 정보 DB 삭제
     * Clears Cache : hldyEntityList, isHldy, isHldyOrWeekend
     */

    @CacheEvict(value = {"hldyEntityList", "isHldy", "isHldyOrWeekend"}, allEntries = true)
    public void delHldyList(final String yyStr) throws Exception {
        Map<String, Object> searchParamMap = new HashMap() {{
            put("searchStartDt", DateUtils.asDate(yyStr + "-01-01"));
            put("searchEndDt", DateParser.eDateParse(DateUtils.asDate(yyStr + "-12-31")));
            put("boardCd", "hldyApi");
        }};
        Page<SchdulEntity> schdulList = schdulService.getListEntity(searchParamMap, Pageable.unpaged());

        schdulRepository.deleteAll(schdulList);
    }
}
