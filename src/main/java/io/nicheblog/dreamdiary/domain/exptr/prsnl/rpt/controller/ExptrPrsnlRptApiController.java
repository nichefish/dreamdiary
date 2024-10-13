package io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.controller;

import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;

/**
 * ExptrPrsnlRptApiController
 * <pre>
 *  경비 관리 > 월간지출내역 API 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class ExptrPrsnlRptApiController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.EXPTR_PRSNL_RPT_ITEMS;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_PRSNL_RPT;        // 작업 카테고리 (로그 적재용)

    /**
     * 경비 관리 > 경비지출누적집계 > 년도별 경비 지출 엑셀 다운로드
     * (관리자MNGR만 접근 가능.)
     */
    // @GetMapping(Url.EXPTR_PRSNL_RPT_XLSX_DOWNLOAD)
    // @Secured(Constant.ROLE_MNGR)
    // public void exptrPrsnlRptXlsxDownload(
    //         final LogActvtyParam logParam,
    //         final @RequestParam("statsYy") String yyStr,
    //         final @RequestParam("statsMnth") String mnthStr
    // ) throws Exception {
//
    //     boolean isSuccess = false;
    //     String rsltMsg = "";
    //     try {
    //         // 년도, 월 담긴 HashMap 생성
    //         Map<String, Object> searchParamMap = cmmService.getYyMhtnMap(yyStr, mnthStr);
    //         // 전체 리스트
    //         List<Object> exptrItemXlsxList = exptrPrsnlRptService.getExptrPrsnlRptItemXlsxList(searchParamMap);
//
    //         // 집계 + 각 계정과목별 항목 존재여부 체크
    //         List<Object> exptrSmXlsxList = exptrPrsnlRptService.getExptrPrsnlRptSmXlsxList(searchParamMap, Pageable.unpaged());
    //         // Map에 담아서 XLSX DOWNLOAD
    //         LinkedHashMap objMap = new LinkedHashMap<>() {{
    //             put("exptrPrsnlRptItem", exptrItemXlsxList);
    //             put("exptrPrsnlRptSm", exptrSmXlsxList);
    //         }};
    //         xlsxUtils.multiListXlxsDownload(Constant.EXPTR_PRSNL_RPT, objMap);
    //         isSuccess = true;
    //         rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
    //     } catch (Exception e) {
    //         isSuccess = false;
    //         rsltMsg = MessageUtils.getExceptionMsg(e);
    //         logParam.setExceptionInfo(e);
    //         MessageUtils.alertMessage(rsltMsg, Url.EXPTR_PRSNL_RPT_ITEMS);
    //     } finally {
    //         // 로그 관련 처리
    //         logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
    //         publisher.publishEvent(new LogActvtyEvent(this, logParam));
    //     }
    // }
}