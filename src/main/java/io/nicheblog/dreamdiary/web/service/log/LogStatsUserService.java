package io.nicheblog.dreamdiary.web.service.log;

import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.mapstruct.log.LogStatsUserMapstruct;
import io.nicheblog.dreamdiary.web.model.log.LogStatsUserDto;
import io.nicheblog.dreamdiary.web.model.log.LogStatsUserIntrfc;
import io.nicheblog.dreamdiary.web.repository.log.LogStatsUserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * LogStatsUserService
 * <pre>
 *  로그 통계 (사용자별) 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("logStatsUserService")
public class LogStatsUserService {

    private final LogStatsUserMapstruct logStatsUserMapstruct = LogStatsUserMapstruct.INSTANCE;

    @Resource(name = "logStatsUserRepository")
    private LogStatsUserRepository logStatsUserRepository;

    /**
     * 활동 로그 > 활동 로그 목록 조회 (entity level)
     */
    public List<LogStatsUserIntrfc> getStatsUserIntrfcList(
            final Map<String, Object> searchParamMap,
            final Pageable pageable
    ) throws Exception {
        // 목록 검색 (기본 조건 :: 당일 시작 ~ 당일 끝)
        if (!searchParamMap.containsKey("searchStartDt")) searchParamMap.put("searchStartDt", DateUtils.getCurrDateStr(DateUtils.PTN_DATE));
        // searchParamMap에서 빈 값들 및 쓸모없는 값들 정리
        Map<String, Object> filteredSearchKey = CmmUtils.Param.filterParamMap(searchParamMap);
        return logStatsUserRepository.getStatsUserIntrfcList((Date) filteredSearchKey.get("searchStartDt"), (Date) filteredSearchKey.get("searchEndDt"));
    }

    /**
     * 활동 로그 > 활동 로그 목록 조회 (dto level)
     */
    public List<LogStatsUserDto> logStatsUserDtoList(
            final Map<String, Object> searchParamMap,
            final Pageable pageable
    ) throws Exception {
        List<LogStatsUserIntrfc> intrfcList = this.getStatsUserIntrfcList(searchParamMap, pageable);

        List<LogStatsUserDto> dtoList = new ArrayList<>();
        for (LogStatsUserIntrfc intrfc : intrfcList) {
            dtoList.add(logStatsUserMapstruct.toDto(intrfc));
        }
        dtoList.sort(Comparator.naturalOrder());
        long i = dtoList.size();
        for (LogStatsUserDto dto : dtoList) {
            dto.setRnum(i--);
        }
        return dtoList;
    }

    public List<LogStatsUserIntrfc> getStatsNotUserIntrfcList(
            final Map<String, Object> searchParamMap,
            final Pageable pageable
    ) throws Exception {
        // 목록 검색 (기본 조건 :: 당일 시작 ~ 당일 끝)
        if (!searchParamMap.containsKey("searchStartDt")) searchParamMap.put("searchStartDt", DateUtils.getCurrDateStr(DateUtils.PTN_DATE));
        // searchParamMap에서 빈 값들 및 쓸모없는 값들 정리
        Map<String, Object> filteredSearchKey = CmmUtils.Param.filterParamMap(searchParamMap);
        return logStatsUserRepository.getStatsNotUserIntrfcList((Date) filteredSearchKey.get("searchStartDt"), (Date) filteredSearchKey.get("searchEndDt"));
    }

    public List<LogStatsUserDto> logStatsNotUserDtoList(
            Map<String, Object> searchParamMap,
            Pageable pageable
    ) throws Exception {
        List<LogStatsUserIntrfc> intrfcList = this.getStatsNotUserIntrfcList(searchParamMap, pageable);

        List<LogStatsUserDto> dtoList = new ArrayList<>();
        for (LogStatsUserIntrfc intrfc : intrfcList) {
            dtoList.add(logStatsUserMapstruct.toDto(intrfc));
        }
        dtoList.sort(Comparator.naturalOrder());
        return dtoList;
    }
}