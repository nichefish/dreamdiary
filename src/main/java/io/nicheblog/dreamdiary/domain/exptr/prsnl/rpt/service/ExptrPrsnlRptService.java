package io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nicheblog.dreamdiary.domain._core.cd.service.DtlCdService;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.mapstruct.ExptrPrsnlItemMapstruct;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.service.ExptrPrsnlPaprService;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.model.ExptrPrsnlRptItemDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.model.ExptrPrsnlRptItemXlsxDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.model.ExptrPrsnlRptSmDtlDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.model.ExptrPrsnlRptSmDto;
import io.nicheblog.dreamdiary.domain.user.info.model.UserDto;
import io.nicheblog.dreamdiary.domain.user.info.service.UserService;
import io.nicheblog.dreamdiary.global.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * ExptrPrsnlRptService
 * <pre>
 *  경비 관리 > 경비지출서 월간지출내역 (보고용) 취합 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("exptrPrsnlRptService")
@RequiredArgsConstructor
@Log4j2
public class ExptrPrsnlRptService {

    private final ExptrPrsnlPaprService exptrPrsnlPaprService;
    private final ExptrPrsnlItemMapstruct exptrPrsnlItemMapstruct = ExptrPrsnlItemMapstruct.INSTANCE;
    private final DtlCdService dtlCdService;
    private final UserService userService;

    /**
     * 경비지출서 월간지출내역 (보고용) 개별 목록 조회
     *
     * @param searchParamMap 검색 파라미터 맵
     * @param pageable 페이지네이션 정보 (Pageable)
     * @return {@link Page} -- 경비지출항목 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    public Page<ExptrPrsnlRptItemDto> getExptrPrsnlRptItemList(
            final Map<String, Object> searchParamMap,
            final Pageable pageable
    ) throws Exception {
        // 목록 검색
        Page<ExptrPrsnlPaprEntity> entityList = exptrPrsnlPaprService.getPageEntity(searchParamMap, pageable);

        // Page<Entity> -> Page<Dto>
        List<ExptrPrsnlRptItemDto> dtoList = new ArrayList<>();
        for (ExptrPrsnlPaprEntity exptr : entityList.getContent()) {
            // 사용자 정보 조회
            String userId = exptr.getRegstrId();
            UserDto user = userService.getDtlDto(userId);
            // UserInfoDto userInfo = user.getUserInfo();
            // 개별 지출 항목 조회
            List<ExptrPrsnlItemEntity> itemList = exptr.getItemList();
            if (CollectionUtils.isEmpty(itemList)) continue;
            for (ExptrPrsnlItemEntity exptrItem : itemList) {
                if ("Y".equals(exptrItem.getRjectYn())) continue;       // 반려 제외
                ExptrPrsnlRptItemDto rptItem = exptrPrsnlItemMapstruct.toRptItemDto(exptrItem);
                // 소속 및 이름 세팅
                // if (userInfo == null) continue;
                // rptItem.setCmpyNm(userInfo.getCmpyNm());
                // rptItem.setUserNm(userInfo.getUserNm());
                // dtoList.add(rptItem);
            }
        }
        return new PageImpl<>(dtoList, entityList.getPageable(), entityList.getTotalElements());
    }

    /**
     * 경비지출서 월간지출내역 (보고용) 집계 목록 조회
     *
     * @param searchParamMap 검색 파라미터 맵
     * @param pageable 페이지네이션 정보 (Pageable)
     * @return {@link Page} -- 월간지출내역 집계 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    public Page<ExptrPrsnlRptSmDto> getExptrPrsnlRptSmList(
            final Map<String, Object> searchParamMap,
            final Pageable pageable
    ) throws Exception {
        // 경비지출서 목록 검색 (entity level)
        Page<ExptrPrsnlPaprEntity> entityList = exptrPrsnlPaprService.getPageEntity(searchParamMap, pageable);

        // 개인별 경비지출내역을 계정항목별로 합산하여 추가
        List<ExptrPrsnlRptSmDto> dtoList = new ArrayList<>();
        for (ExptrPrsnlPaprEntity exptr : entityList) {
            ExptrPrsnlRptSmDto exptrSm = new ExptrPrsnlRptSmDto();

            // 사용자 정보 세팅
            UserDto user = userService.getDtlDto(exptr.getRegstrId());
            exptrSm.setUserInfo(user);      // 사용자 정보 세팅 (메소드 분리)
            // 총액, 영수증cnt 및 계정과목별 액수 집계
            List<ExptrPrsnlItemEntity> itemList = exptr.getItemList();

            // 계정과목별 합산액 hashMap에 저장 후 List<dto>로 변환해서 세팅 (메소드 분리)
            Map<String, Integer> exptrAmtMap = this.getExptrTySmMap(itemList);
            if (MapUtils.isEmpty(exptrAmtMap)) continue;
            List<ExptrPrsnlRptSmDtlDto> exptrTySmList = this.getExptrSmDtlList(exptrAmtMap);
            // 계정과목별 합산 정보 세팅
            exptrSm.setItemList(exptrTySmList);
            // 집계 끝난 dto 목록에 추가
            dtoList.add(exptrSm);
        }

        return new PageImpl<>(dtoList, Pageable.unpaged(), dtoList.size());
    }

    /**
     * 경비지출서 월간지출내역 (보고용) 개인-계정과목별 총액 합산 (메소드 분리)
     *
     * @param itemList 경비지출항목 목록
     * @return {@link Map} -- 계정과목별 총액을 담은 맵
     */
    public Map<String, Integer> getExptrTySmMap(final List<ExptrPrsnlItemEntity> itemList) {
        if (CollectionUtils.isEmpty(itemList)) return null;

        Map<String, Integer> exptrAmtMap = new HashMap<>();
        for (ExptrPrsnlItemEntity item : itemList) {
            if ("Y".equals(item.getRjectYn())) continue;

            String exptrCd = item.getExptrCd();
            Integer exptrTySmAmt = item.getExptrAmt();
            if (exptrAmtMap.containsKey(exptrCd)) exptrTySmAmt += exptrAmtMap.get(exptrCd);
            exptrAmtMap.put(exptrCd, exptrTySmAmt);
        }
        return exptrAmtMap;
    }

    /**
     * 경비지출서 월간지출내역 (보고용) 개인-계정과목별 총액 목록 생성 (메소드 분리)
     *
     * @param exptrAmtMap 계정과목별 총액을 담은 맵
     * @return {@link List} -- 계정과목별 총액을 담은 DTO 목록
     */
    private List<ExptrPrsnlRptSmDtlDto> getExptrSmDtlList(final Map<String, Integer> exptrAmtMap) {
        List<ExptrPrsnlRptSmDtlDto> exptrTySmList = new ArrayList<>();
        for (String key : exptrAmtMap.keySet()) {
            String exptrTyNm = dtlCdService.getDtlCdNm(Constant.EXPTR_CD, key);
            ExptrPrsnlRptSmDtlDto item = new ExptrPrsnlRptSmDtlDto(key, exptrTyNm, exptrAmtMap.get(key));
            exptrTySmList.add(item);
        }
        return exptrTySmList;
    }

    /**
     * 경비지출서 월간지출내역 (보고용) 개인-계정과목별 계정과목 목록 생성 (메소드 분리)
     *
     * @param exptrSmList 경비지출내역 집계 목록
     * @return {@link List} -- 개인별 소배 계정과목이름 목록
     */
    public List<String> getExptrTyList(final Page<ExptrPrsnlRptSmDto> exptrSmList) {
        List<String> exptrTyList = new ArrayList<>();
        if (exptrSmList == null) return exptrTyList;

        List<ExptrPrsnlRptSmDto> smList = exptrSmList.getContent();
        if (CollectionUtils.isEmpty(smList)) return exptrTyList;

        for (ExptrPrsnlRptSmDto rptSm : smList) {
            List<ExptrPrsnlRptSmDtlDto> itemList = rptSm.getItemList();
            if (CollectionUtils.isEmpty(itemList)) continue;
            for (ExptrPrsnlRptSmDtlDto item : itemList) {
                String exptrCd = item.getExptrCd();
                String exptrTyNm = dtlCdService.getDtlCdNm(Constant.EXPTR_CD, exptrCd);
                if (!exptrTyList.contains(exptrTyNm)) exptrTyList.add(exptrTyNm);
            }
        }
        return exptrTyList;
    }

    /**
     * 경비지출서 월간지출내역 (보고용) 개별항목 엑셀 다운로드 목록 조회
     */
    public List<Object> getExptrPrsnlRptItemXlsxList(final Map<String, Object> searchParamMap) throws Exception {
        Page<ExptrPrsnlRptItemDto> exptrItemList = this.getExptrPrsnlRptItemList(searchParamMap, Pageable.unpaged());
        List<Object> exptrItemXlsxList = new ArrayList<>();
        int exptrRptItemSm = 0;
        for (ExptrPrsnlRptItemDto exptr : exptrItemList) {
            String exptrAmtStr = exptr.getExptrAmt();
            if (StringUtils.isEmpty(exptrAmtStr)) continue;
            try {
                exptrRptItemSm += Integer.parseInt(exptrAmtStr);
            } catch (Exception e) {
                //
            }
            exptrItemXlsxList.add(exptrPrsnlItemMapstruct.toRptItemXlsxDto(exptr));
            // TODO : 정렬?
        }
        ExptrPrsnlRptItemXlsxDto exptrRptItemSmDto = new ExptrPrsnlRptItemXlsxDto();
        exptrRptItemSmDto.setExptrCn("총합");
        exptrRptItemSmDto.setExptrAmt(exptrRptItemSm);
        exptrItemXlsxList.add(exptrRptItemSmDto);
        return exptrItemXlsxList;
    }

    /**
     * 경비지출서 월간지출내역 (보고용) 집계 엑셀 다운로드 목록 조회
     * 가로 행이 가변적이기 때문에 기존처럼 Dto를 이용하는 대신 Map을 이용하여 전달한다.
     *
     * @param searchParamMap 검색 파라미터 맵
     * @return {@link List} -- 개별 항목의 엑셀 다운로드 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    public List<Object> getExptrPrsnlRptSmXlsxList(
            final Map<String, Object> searchParamMap,
            final Pageable unpaged
    ) throws Exception {
        Page<ExptrPrsnlRptSmDto> exptrSmList = this.getExptrPrsnlRptSmList(searchParamMap, unpaged);
        List<String> exptrTyList = this.getExptrTyList(exptrSmList);

        ObjectMapper mapper = new ObjectMapper();
        List<Object> resultMap = new ArrayList<>();

        LinkedHashMap header = new LinkedHashMap<>() {{
            put("소속", "소속");
            put("이름", "이름");
            put("은행", "은행");
            put("계좌", "계좌");
            put("총합계", "총합계");
            put("영수증 스캔", "영수증 스캔");
            put("영수증 원본", "영수증 원본");
        }};
        for (String key : exptrTyList) {
            header.put(key, key);
        }
        resultMap.add(header);

        Integer rptSm = 0;      // 총합계
        Map<String, Integer> exptrTySmMap = new HashMap<>();     // 개별 항목별 합산은 Map에 저장
        for (ExptrPrsnlRptSmDto exptr : exptrSmList) {
            rptSm += exptr.getTotAmt();          // 합계 더하기

            Double itemCnt = Double.valueOf(exptr.getItemCnt());
            double atchRatio = exptr.getAtchRciptCnt() / itemCnt;
            double rciptRatio = exptr.getOrgnlRciptCnt() / itemCnt;
            String atchYn = (atchRatio == 1) ? "○" : (atchRatio) < 1 ? "△" : "×";
            String rciptYn = (rciptRatio == 1) ? "○" : (rciptRatio) < 1 ? "△" : "×";

            // 키값 순서를 유지하기 위해 linkedHashMap 사용
            LinkedHashMap<String, Object> result = mapper.convertValue(exptr, LinkedHashMap.class);
            result.put("atchYn", atchYn);
            result.put("rciptYn", rciptYn);
            result.remove("itemList");
            result.remove("itemCnt");
            result.remove("atchRciptCnt");
            result.remove("atchRciptStus");
            result.remove("orgnlRciptCnt");
            result.remove("orgnlRciptNotNeededCnt");
            result.remove("orgnlRciptStus");
            List<ExptrPrsnlRptSmDtlDto> itemList = exptr.getItemList();
            for (String key : exptrTyList) {
                result.putIfAbsent(key, 0);
                exptrTySmMap.putIfAbsent(key, 0);
                for (ExptrPrsnlRptSmDtlDto item : itemList) {
                    String exptrTyNm = item.getExptrTyNm();
                    if (key.equals(exptrTyNm)) {
                        result.put(key, item.getExptrSmAmt());
                        exptrTySmMap.put(key, exptrTySmMap.get(key) + item.getExptrSmAmt());
                        break;
                    }
                }
            }
            resultMap.add(result);
            // TODO : 정렬?
        }
        LinkedHashMap<String, Object> exptrTySmObj = new LinkedHashMap<>();
        exptrTySmObj.put("cmpyNm", "");
        exptrTySmObj.put("userNm", "");
        exptrTySmObj.put("acntBank", "");
        exptrTySmObj.put("acntNo", "");
        exptrTySmObj.put("totAmt", rptSm);
        exptrTySmObj.put("atchYn", "");
        exptrTySmObj.put("rciptYn", "");
        Set<String> keyset = exptrTySmMap.keySet();
        for (String exptrTy : exptrTyList) {
            for (String key : keyset) {
                if (exptrTy.equals(key)) exptrTySmObj.put(key, exptrTySmMap.get(key));
            }
        }
        resultMap.add(exptrTySmObj);
        log.info("resultMap: {}", resultMap);
        return resultMap;
    }

    /**
     * 경비지출서 월간지출내역 (보고용) 개별 목록 합산값 조회
     *
     * @param exptrItemList 경비지출항목 목록
     * @return {@link Integer} -- 개별 항목 금액의 합산값
     */
    public Integer getExptrRptItemSm(final Page<ExptrPrsnlRptItemDto> exptrItemList) {
        if (exptrItemList == null || CollectionUtils.isEmpty(exptrItemList.getContent())) return null;
        int exptrRptItemSm = 0;
        for (ExptrPrsnlRptItemDto exptr : exptrItemList) {
            String exptrAmtStr = exptr.getExptrAmt();
            if (StringUtils.isEmpty(exptrAmtStr)) continue;
            try {
                exptrRptItemSm += Integer.parseInt(exptrAmtStr);
            } catch (Exception e) {
                //
            }
        }
        return exptrRptItemSm;
    }

    /**
     * 경비지출서 월간지출내역 (보고용) 집계 목록 (항목별) 합산값 조회
     *
     * @param exptrSmList 경비지출서 집계 목록
     * @param exptrTyList 조회할 항목 이름 목록
     * @return {@link Integer} -- 항목별 합산값을 담은 맵
     */
    public Map<String, Integer> getExptrRptTySmMap(
            final Page<ExptrPrsnlRptSmDto> exptrSmList,
            final List<String> exptrTyList
    ) {
        if (exptrSmList == null || CollectionUtils.isEmpty(exptrSmList.getContent())) return null;
        int totSm = 0;
        Map<String, Integer> exptrTySmMap = new HashMap<>();     // 개별 항목별 합산은 Map에 저장
        for (ExptrPrsnlRptSmDto exptr : exptrSmList) {
            int exptrAmt = exptr.getTotAmt() != null ? exptr.getTotAmt() : 0;
            totSm += exptrAmt;
            List<ExptrPrsnlRptSmDtlDto> itemList = exptr.getItemList();
            if (CollectionUtils.isEmpty(itemList)) continue;
            for (String key : exptrTyList) {
                exptrTySmMap.putIfAbsent(key, 0);
                for (ExptrPrsnlRptSmDtlDto item : itemList) {
                    String exptrTyNm = item.getExptrTyNm();
                    if (key.equals(exptrTyNm)) {
                        exptrTySmMap.put(key, exptrTySmMap.get(key) + item.getExptrSmAmt());
                        break;
                    }
                }
            }
        }
        exptrTySmMap.put("totSm", totSm);
        return exptrTySmMap;
    }
}