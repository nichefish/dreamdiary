package io.nicheblog.dreamdiary.web.service.schdul;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulEntity;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.mapstruct.schdul.SchdulCalMapstruct;
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.schdul.VcatnSchdulMapstruct;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulCalDto;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulSearchParam;
import io.nicheblog.dreamdiary.web.repository.schdul.jpa.SchdulRepository;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import io.nicheblog.dreamdiary.web.service.vcatn.schdul.VcatnSchdulService;
import io.nicheblog.dreamdiary.web.spec.schdul.SchdulSpec;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SchdulCalService
 * 일정 달력 서비스 모듈
 *
 * @author nichefish
 */
@Service("schdulCalService")
@Log4j2
public class SchdulCalService {

    @Resource(name = "schdulService")
    private SchdulService schdulService;

    @Resource(name = "vcatnSchdulService")
    private VcatnSchdulService vcatnSchdulService;

    private final SchdulCalMapstruct schdulCalMapstruct = SchdulCalMapstruct.INSTANCE;
    private final VcatnSchdulMapstruct vcatnSchdulMapstruct = VcatnSchdulMapstruct.INSTANCE;

    @Resource(name = "schdulSpec")
    private SchdulSpec schdulSpec;

    @Resource(name = "userService")
    private UserService userService;

    // @Resource(name = "vcatnSchdulService")
    // private VcatnSchdulService vcatnSchdulService;

    @Resource(name = "schdulRepository")
    private SchdulRepository schdulRepository;

    /**
     * 일정 > 전체일정 (일정+휴가) 데이터 조회
     */
    public List<SchdulCalDto> getSchdulTotalCalList(final SchdulSearchParam searchParam) throws Exception {
        List<SchdulCalDto> totalSchdulCalList = new ArrayList<>();

        // 휴가 달력 목록 검색
        String vcatnChk = searchParam.getVcatnChked();
        if ("Y".equals(vcatnChk)) {
            List<SchdulCalDto> vcatnCalList = this.getVcatnCalList(searchParam);
            totalSchdulCalList.addAll(vcatnCalList);
        }

        // 생일 달력 목록 검색
        // List<SchdulCalDto> brthdyCalList = this.getBrthdyCalList(searchParam);
        // totalSchdulCalList.addAll(brthdyCalList);

        // 일정(공휴일, 행사) 달력 목록 검색
        List<SchdulCalDto> hldyCalList = this.getHldyCalList(searchParam);
        totalSchdulCalList.addAll(hldyCalList);

        // 일정(재택근무, 외근) 달력 목록 검색
        List<SchdulCalDto> schdulCalList = this.getSchdulCalList(searchParam);
        totalSchdulCalList.addAll(schdulCalList);

        // 개인 일정 달력 목록 검색
        boolean prvtChked = "Y".equals(searchParam.getPrvtChked());
        if (prvtChked) {
            List<SchdulCalDto> prvtCalList = this.getPrvtCalList(searchParam);
            totalSchdulCalList.addAll(prvtCalList);
        }

        totalSchdulCalList.sort(Comparator.naturalOrder());
        return totalSchdulCalList;
    }

    /**
     * 일정 > 휴가 데이터 조회
     */
    public List<SchdulCalDto> getVcatnCalList(final SchdulSearchParam searchParam) throws Exception {
        String bgnDt = searchParam.getBgnDt();
        String endDt = searchParam.getEndDt();
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("searchStartDt", bgnDt);
            put("searchEndDt", endDt);
        }};

        // 휴가 목록 검색
        List<VcatnSchdulEntity> vcatnEntityList = vcatnSchdulService.getListEntity(searchParamMap);
        List<SchdulCalDto> vcatnCalList = new ArrayList<>();
        for (VcatnSchdulEntity vcatn : vcatnEntityList) {
            Date vcatnEndDt = DateUtils.Parser.sDateParse(vcatn.getEndDt());

            // 로직 :: 날짜 훑으면서 각 일자별로 쪼갬. 공휴일 또는 주말여부 체크
            Date keyDt = DateUtils.Parser.sDateParse(vcatn.getBgnDt());
            // 1. 첫날 객체부터 시작
            SchdulCalDto calDto = schdulCalMapstruct.toCalDto(vcatn);
            boolean isDelimStart = true;
            assert (keyDt != null && vcatnEndDt != null);
            // 2. 주말/공휴일 제외하고 출력하는 로직 :: 하루씩 순회하며 주말여부 체크
            while (keyDt != null && keyDt.compareTo(vcatnEndDt) <= 0) {
                Boolean isHldy = schdulService.isHldyOrWeekend(keyDt);
                boolean wasHldy = schdulService.isHldyOrWeekend(DateUtils.getDateAddDay(keyDt, -1));
                boolean changedToHldy = !wasHldy && isHldy;
                boolean isEndDt = DateUtils.isSameDay(keyDt, vcatnEndDt);

                if (!isHldy) {
                    // 휴일이 아니면? 날짜세기 시작
                    if (isDelimStart) {
                        calDto = this.initNewCalDto(vcatn, keyDt);
                        isDelimStart = false;
                    }
                    // 끝일자면? 날짜세기 마무리
                    if (isEndDt) {
                        this.finNewCalDto(calDto, keyDt);
                        vcatnCalList.add(calDto);
                        isDelimStart = true;
                    }
                } else {
                    // 휴일이면? 이전일자 기준으로 끊어간다.
                    if (!isDelimStart && changedToHldy) {
                        this.finNewCalDto(calDto, DateUtils.getDateAddDay(keyDt, -1));
                        vcatnCalList.add(calDto);
                        isDelimStart = true;
                    }
                }
                // 위 조건에 해당 안할시? 다음날짜로 넘어간다.
                keyDt = DateUtils.getDateAddDay(keyDt, 1);
            }
        }
        return vcatnCalList;
    }

    /**
     * CalDto 생성 :: 메소드 분리
     */
    private SchdulCalDto initNewCalDto(VcatnSchdulEntity vcatn, Date keyDt) throws Exception {
        SchdulCalDto calDto = schdulCalMapstruct.toCalDto(vcatn);
        calDto.setBgnDt(DateUtils.asStr(keyDt, DatePtn.DATE));
        if (StringUtils.isEmpty(calDto.getSchdulCd())) calDto.setSchdulCd(Constant.SCHDUL_VCATN);
        if (StringUtils.isEmpty(calDto.getClassName())) calDto.setClassName("cursor-pointer fc-event-danger fc-event-solid-warning");
        return calDto;
    }
    /**
     * CalDto 마무리 :: 메소드 분리
     */
    private void finNewCalDto(SchdulCalDto calDto, Date keyDt) throws Exception {
        calDto.setEndDt(DateUtils.Parser.eDateParseStr(keyDt));
        // 1일짜리 일정일 경우 : allday=true로 줘야 제대로 나온다.
        boolean isSameDay = DateUtils.isSameDay(calDto.getBgnDt(), calDto.getEndDt());
        if (isSameDay) calDto.setAllDay(true);
    }

    /**
     * 일정 > 생일 달력 조회
     */
    //private List<SchdulCalDto> getBrthdyCalList(final SchdulSearchParam searchParam) throws Exception {
    //    List<SchdulCalDto> brthdyCalList = new ArrayList<>();
    //    // 생일인 직원 목록 조회
    //    List<UserDto.LIST> brthdyUserList = userService.getCrdtUserList(searchParam.getBgnDt(), searchParam.getEndDt());
    //    if (CollectionUtils.isEmpty(brthdyUserList)) return new ArrayList<>();
    //    for (UserDto.LIST user : brthdyUserList) {
    //        if ("Y".equals(user.getRetireYn())) continue;
    //        String brthdyStr = user.getBrthdy();
    //        if (StringUtils.isEmpty(brthdyStr)) continue;
    //        String thisBrthdyStr = DateUtils.getCurrYyStr() + brthdyStr.substring(4);
    //        // 음력 / 양력 구분해서 적용
    //        if ("Y".equals(user.getLunarYn())) {
    //            // 음력일 경우 = 1) 올해의 음력 생일 날짜 구해서 양력으로 변환
    //            thisBrthdyStr = DateUtils.ChineseCal.lunToSolStr(thisBrthdyStr, DateUtils.PTN_DATE);
    //        }
    //        String schdulNm = "\uD83C\uDF89" + user.getUserNm() + " 생일";
    //        if ("Y".equals(user.getLunarYn())) {
    //            schdulNm += " (음력)";
    //        }
    //        SchdulCalDto calDto = new SchdulCalDto(schdulNm, thisBrthdyStr, Constant.SCHDUL_BRTHDY);
    //        brthdyCalList.add(calDto);
    //    }
    //     return brthdyCalList;
    // }

    /**
     * 일정(공휴일, 행사) 달력 목록 검색
     */
    private List<SchdulCalDto> getHldyCalList(final SchdulSearchParam searchParam) throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("searchStartDt", searchParam.getBgnDt());
            put("searchEndDt", searchParam.getEndDt());
            put("getHldyCeremonyOnly", true);
        }};

        // 일정 목록 검색
        List<SchdulEntity> schdulEntityList = schdulRepository.findAll(schdulSpec.searchWith(searchParamMap));
        return schdulEntityList.stream()
                .map(entity -> {
                    try {
                        return schdulCalMapstruct.toCalDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 일정(재택근무, 내근, 외근) 달력 목록 검색
     */
    public List<SchdulCalDto> getSchdulCalList(final SchdulSearchParam searchParam) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        Map<String, Object> filteredSearchKey = CmmUtils.Param.filterParamMap(searchParamMap);
        // 일정 목록 검색
        List<SchdulEntity> schdulEntityList = schdulRepository.findAll(schdulSpec.searchWith(filteredSearchKey));
        return schdulEntityList.stream()
                .map(entity -> {
                    try {
                        return schdulCalMapstruct.toCalDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 개인 일정 달력 목록 검색
     */
    public List<SchdulCalDto> getPrvtCalList(final SchdulSearchParam searchParam) throws Exception {

        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("searchStartDt", searchParam.getBgnDt());
            put("searchEndDt", searchParam.getEndDt());
            put("getPrvtOnly", true);
        }};

        // 일정 목록 검색
        List<SchdulEntity> schdulEntityList = schdulRepository.findAll(schdulSpec.searchWith(searchParamMap));
        return schdulEntityList.stream()
                .map(entity -> {
                    try {
                        return schdulCalMapstruct.toCalDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

}
