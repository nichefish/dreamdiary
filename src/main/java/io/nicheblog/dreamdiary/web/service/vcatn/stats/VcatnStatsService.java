package io.nicheblog.dreamdiary.web.service.vcatn.stats;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.entity.vcatn.stats.VcatnStatsEntity;
import io.nicheblog.dreamdiary.web.entity.vcatn.stats.VcatnStatsKey;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserMapstruct;
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.stats.VcatnStatsMapstruct;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsDto;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsTotalDto;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.web.repository.vcatn.jpa.VcatnStatsRepository;
import io.nicheblog.dreamdiary.web.service.schdul.SchdulService;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import io.nicheblog.dreamdiary.web.service.vcatn.schdul.VcatnSchdulService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * VcatnStatsService
 * <pre>
 *  휴가관리 > 년도별 휴가관리 통계 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("vcatnStatsService")
@Log4j2
public class VcatnStatsService {

    private final VcatnStatsMapstruct vcatnStatsMapstruct = VcatnStatsMapstruct.INSTANCE;
    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "vcatnSchdulService")
    private VcatnSchdulService vcatnSchdulService;

    @Resource(name = "vcatnStatsYyService")
    private VcatnStatsYyService vcatnStatsYyService;

    @Resource(name = "vcatnStatsRepository")
    private VcatnStatsRepository vcatnStatsRepository;

    @Resource(name = "schdulService")
    private SchdulService schdulService;

    /**
     * 휴가관리 > 년도별 휴가관리 통계 목록 조회
     * 해당년도에 근무이력이 있는(중도퇴사 포함) 모든 직원(재직+프리랜서) 전원에 대하여 산정
     */
    public List<VcatnStatsDto> getVcatnStatsList(final VcatnStatsYyDto statsYy) throws Exception {
        String yyStr = statsYy.getStatsYy();
        // 직원목록 조회 :: 해당년도에 근무이력이 있는(중도퇴사 포함) 모든 직원(재직+프리랜서) 전원에 대하여 산정
        List<UserDto.LIST> userList = userService.getCrdtUserList(yyStr);

        List<VcatnStatsDto> stats = new ArrayList<>();
        for (UserDto.LIST user : userList) {
            // 각 직원에 대하여 휴가일자 산정하여 목록 반환 :: 메소드 분리
            VcatnStatsDto vcantStats = this.getVcatnStats(user, statsYy);
            stats.add(vcantStats);
        }
        return stats;
    }

    /**
     * 휴가관리 > (개인) 휴가현황 조회
     */
    // public VcatnStatsDto getVcatnStatsDtl(
    //         final VcatnStatsYyDto statsYy,
    //         final String userId
    // ) throws Exception {
    //     UserDto user = userService.getDtlDto(userId);
    //     UserDto.LIST userDtl = userMapstruct.dtlDtoToListDto(user);
    //     if (StringUtils.isEmpty(userDtl.getEcnyDt())) throw new NullPointerException("입사일 정보가 존재하지 않습니다.");
    //     return this.getVcatnStats(userDtl, statsYy);
    // }

    /**
     * 휴가관리 > 년도별 휴가관리 통계 > 개인별 휴가 현황 조회 (메소드 분리)
     */
    private VcatnStatsDto getVcatnStats(
            final UserDto.LIST user,
            final VcatnStatsYyDto statsYy
    ) throws Exception {
        // 입사일, 기준일로 근속년수 산청
        LocalDate ecnyDt = DateUtils.asLocalDate(user.getEcnyDt());
        LocalDate stdrdDt = DateUtils.asLocalDate(DateUtils.getDateAddDay(statsYy.getBgnDt(), -1));
        Period ecnyPeriod = Period.between(ecnyDt, stdrdDt);
        // 1. 입사일부터 기준일까지 산정하여 내년 연차 산정
        int bsYryc = 0, newbieMnthYryc = 0;
        int basicBsYryc = 15;
        // 신입(~입사 1년차) = 입사 1년차까지 1달에 1개 휴가 발생
        // 신입 이외 = 기본연자 15개
        int workYy = ecnyPeriod.getYears();
        boolean isNewbie = (workYy == 0);
        if (!isNewbie) {
            bsYryc = basicBsYryc;
        } else {
            boolean ecnyBeforeStdrdDt = ecnyDt.isBefore(stdrdDt);
            if (ecnyBeforeStdrdDt) {
                // 기준일 이전에 입사한 경우 = 기준일~입사일+1년까지는 1달에 1개, 그 이후로는 연차 계산 (기간에 따른 비율)
                LocalDate newbieOutDt = DateUtils.asLocalDate(DateUtils.getDateAddYy(ecnyDt, 1));
                Period newbiePeriod = Period.between(stdrdDt, newbieOutDt);
                newbieMnthYryc = Math.min(newbiePeriod.getMonths(), 11);
                int afterNewbieDay = Math.max((int) ChronoUnit.DAYS.between(newbieOutDt, DateUtils.asLocalDate(statsYy.getEndDt())), 0);
                bsYryc = (afterNewbieDay * 15) / 365;
            } else {
                // 기준일 이후에 입사한 경우 = 입사일~종료일까지 1달에 1개
                Period newbiePeriod = Period.between(ecnyDt, DateUtils.asLocalDate(statsYy.getEndDt()));
                int ecnyMnth = (int) newbiePeriod.toTotalMonths();
                newbieMnthYryc = Math.min(ecnyMnth, 11);
            }
        }
        // 사용자 ID로 검색한 휴가 일정을 불러온다.
        String userId = user.getUserId();
        String yyStr = statsYy.getStatsYy();
        VcatnStatsKey key = new VcatnStatsKey(yyStr, userId);
        Optional<VcatnStatsEntity> entityWrapper = vcatnStatsRepository.findById(key);
        VcatnStatsEntity rsEntity = entityWrapper.orElse(new VcatnStatsEntity());
        VcatnStatsDto dto = vcatnStatsMapstruct.toDto(rsEntity);
        if (StringUtils.isEmpty(dto.getUserId())) dto.setUserId(userId);
        dto.setUser(user);
        dto.setCnwkYy(workYy);
        dto.setBsYryc(bsYryc);
        if (isNewbie) {
            dto.setIsNewbie(isNewbie);
            dto.setNewbieYryc(newbieMnthYryc);
        }
        // 소진연차를 구해야 한다.
        Map<String, Object> searchParamMap = vcatnStatsYyService.getVcatnYyDtMap(statsYy);
        Date statsYyBgnDt = DateUtils.asDate(searchParamMap.get("searchStartDt"));
        Date statsYyEndDt = DateUtils.asDate(searchParamMap.get("searchEndDt"));
        dto.setStatsDt(DateUtils.getDateAddDayStr(statsYyBgnDt, DatePtn.DATE, -1));
        searchParamMap.put("userId", userId);
        List<VcatnSchdulEntity> vcatnEntityList = vcatnSchdulService.getListEntity(searchParamMap);
        double totVcatnSchdul = 0.0;
        for (VcatnSchdulEntity vcatn : vcatnEntityList) {
            // 연차(반차)만 계산한다. 공가, 무급/생리휴가, 경조휴가 등은 패스
            String vcatnTy = vcatn.getVcatnCd();
            List<String> nonAnnuals = Arrays.asList(Constant.VCATN_PBLEN, Constant.VCATN_UNPAID, Constant.VCATN_MNSTR, Constant.VCATN_CTSNN);
            if (nonAnnuals.contains((vcatnTy))) continue;
            // 반차는 0.5일, 연차는 1일 소진
            boolean isHalf = (Constant.VCATN_AM_HALF.equals(vcatnTy) || Constant.VCATN_PM_HALF.equals(vcatnTy));
            double exhrDy = isHalf ? 0.5 : Constant.VCATN_ANNUAL.equals(vcatnTy) ? 1 : 0;
//
            // 휴가 시작일자/종료일자 산정 : 2월 28일 전- 또는 3월 1일 후 식으로 경계에 걸쳐있는 경우를 따진다. (딱 경계까지만 일수 계산하기)
            Date vcatnBgnDt = DateUtils.Parser.sDateParse(vcatn.getBgnDt());
            Date vcatnEndDt = DateUtils.Parser.sDateParse(vcatn.getEndDt());
            assert vcatnBgnDt != null;
            if (vcatnBgnDt.compareTo(statsYyBgnDt) < 0) vcatnBgnDt = statsYyBgnDt;
            vcatnEndDt = (vcatnEndDt == null) ? vcatnBgnDt : (vcatnEndDt.compareTo(statsYyEndDt) > 0) ? statsYyEndDt : vcatnEndDt;
//
            // 날짜 훑으면서 공휴일 또는 주말여부 체크
            Date keyDt = DateUtils.Parser.sDateParse(vcatnBgnDt);
            while (keyDt.compareTo(vcatnEndDt) <= 0) {
                if (schdulService.isHldyOrWeekend(keyDt)) {
                    keyDt = DateUtils.getDateAddDay(keyDt, 1);
                    continue;
                }
                totVcatnSchdul += exhrDy;
                keyDt = DateUtils.getDateAddDay(keyDt, 1);
            }
        }
        dto.setExhsYryc(totVcatnSchdul);
        return dto;
    }

    /**
     * 휴가관리 > 년도별 휴가관리 통계 > 휴가 현황 저장
     */
    public boolean regStatsTotal(final VcatnStatsTotalDto vcatnStatsTotal) throws Exception {
        List<VcatnStatsDto> statsList = vcatnStatsTotal.getStatsList();
        List<VcatnStatsEntity> statsEntityList = new ArrayList<>();
        for (VcatnStatsDto stats : statsList) {
            if (StringUtils.isEmpty(stats.getUserId())) continue;
            stats.setStatsYy(vcatnStatsTotal.getStatsYy());
            VcatnStatsKey key = new VcatnStatsKey(stats.getStatsYy(), stats.getUserId());
            Optional<VcatnStatsEntity> entityWrapper = vcatnStatsRepository.findById(key);
            VcatnStatsEntity rsEntity = entityWrapper.orElse(new VcatnStatsEntity());
            vcatnStatsMapstruct.updateFromDto(stats, rsEntity);
            statsEntityList.add(rsEntity);
        }
        vcatnStatsRepository.saveAll(statsEntityList);
        return true;
    }

    /**
     * 휴가관리 > 년도별 휴가관리 통계 엑셀 다운로드 목록 조회
     * 해당년도에 근무이력이 있는(중도퇴사 포함) 모든 직원(재직+프리랜서) 전원에 대하여 산정
     */
    // public List<Object> getVcatnStatsListXlsx(final VcatnStatsYyDto statsYy) throws Exception {
    //     List<VcatnStatsDto> statsList = this.getVcatnStatsList(statsYy);
    //     List<Object> statsObjList = new ArrayList<>();
    //     for (VcatnStatsDto stats : statsList) {
    //         statsObjList.add(vcatnStatsMapstruct.toListXlsxDto(stats));
    //     }
    //     return statsObjList;
    // }
}
