package io.nicheblog.dreamdiary.api.kasi.service;

import io.nicheblog.dreamdiary.api.kasi.mapstruct.HldyKasiApiMapstruct;
import io.nicheblog.dreamdiary.api.kasi.model.HldyKasiApiItemDto;
import io.nicheblog.dreamdiary.api.kasi.model.HldyKasiApiRespDto;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulEntity;
import io.nicheblog.dreamdiary.web.service.schdul.SchdulService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HldyKasiApiService
 * <pre>
 *  API:: 한국천문연구원(KASI):: 휴일 정보 조회 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service
@Log4j2
public class HldyKasiApiService {

    private final HldyKasiApiMapstruct hldyApiMapstruct = HldyKasiApiMapstruct.INSTANCE;

    @Resource(name = "schdulService")
    private SchdulService schdulService;

    @Value("${api.kasi.serviceKey}")
    private String serviceKey;
    @Value("${api.kasi.api-url}")
    private String serviceUrl;

    /**
     * API:: 한국천문연구원(KASI):: 휴일 정보 조회
     */
    public List<HldyKasiApiItemDto> getHldyList(final String yyParam) throws Exception {
        String yyStr = !StringUtils.isEmpty(yyParam) ? yyParam : DateUtils.getCurrYearStr();
        RestTemplate restTemplate = new RestTemplate();
        List<HldyKasiApiItemDto> rsItems = new ArrayList<>();
        try {
            // URL 설정
            int numOfRows = 30;
            URI requestURI = new URI(serviceUrl + "?solYear=" + yyStr + "&numOfRows=" + numOfRows + "&ServiceKey=" + serviceKey);
            // 요청 생성
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
        if (CollectionUtils.isEmpty(hldyApiList)) return true;
        // dto to entity
        List<SchdulEntity> schdulList = hldyApiList.stream()
                .map(hldy -> {
                    try {
                        return hldyApiMapstruct.toEntity(hldy);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        schdulService.registAll(schdulList);

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
            put("searchEndDt", DateUtils.Parser.eDateParse(DateUtils.asDate(yyStr + "-12-31")));
            put("src", "KASI");
        }};
        schdulService.deleteAll(searchParamMap);
    }
}
