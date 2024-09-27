package io.nicheblog.dreamdiary.web.controller.exptr.prsnl.rpt;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.rpt.ExptrPrsnlRptItemDto;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.rpt.ExptrPrsnlRptSmDto;
import io.nicheblog.dreamdiary.web.service.exptr.prsnl.papr.ExptrPrsnlPaprService;
import io.nicheblog.dreamdiary.web.service.exptr.prsnl.rpt.ExptrPrsnlRptService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ExptrPrsnlRptController
 * <pre>
 *  경비 관리 > 월간지출내역 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class ExptrPrsnlRptController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.EXPTR_PRSNL_RPT_ITEMS;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_PRSNL_RPT;        // 작업 카테고리 (로그 적재용)

    private final ExptrPrsnlPaprService exptrPrsnlPaprService;
    private final ExptrPrsnlRptService exptrPrsnlRptService;

    /**
     * 경비 관리 > 월간지출내역 > 년도별 경비지출 누적집계 화면
     * 관리자MNGR만 접근 가능
     */
    @GetMapping(Url.EXPTR_PRSNL_RPT_ITEMS)
    @Secured(Constant.ROLE_MNGR)
    public String exptrPrsnlRpt(
            final LogActvtyParam logParam,
            final @RequestParam("statsYy") @Nullable String yyStr,
            final @RequestParam("statsMnth") @Nullable String mnthStr,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_RPT.setAcsPageInfo(Constant.PAGE_STATS));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 경비지출서 최저년도~올해년도 목록 조회
            model.addAttribute("yyList", exptrPrsnlPaprService.getExptrPrsnlYyList());
            // 년도, 월 담긴 HashMap 생성
            Map<String, Object> searchParamMap = CmmUtils.getYyMhtnMap(yyStr, mnthStr);
            // 전체 리스트 + 전체 리스트의 합
            Page<ExptrPrsnlRptItemDto> exptrItemList = exptrPrsnlRptService.getExptrPrsnlRptItemList(searchParamMap, Pageable.unpaged());
            if (exptrItemList != null) model.addAttribute("exptrRptItemList", exptrItemList.getContent());
            Integer exptrRptItemSm = exptrPrsnlRptService.getExptrRptItemSm(exptrItemList);
            if (exptrRptItemSm != null) model.addAttribute("exptrRptItemSm", exptrRptItemSm);
            // 집계 리스트
            Page<ExptrPrsnlRptSmDto> exptrSmList = exptrPrsnlRptService.getExptrPrsnlRptSmList(searchParamMap, Pageable.unpaged());
            if (exptrSmList != null) model.addAttribute("exptrRptSmList", exptrSmList.getContent());
            // 각 계정과목별 항목 존재여부 체크 (존재하는 것만 화면 출력)
            List<String> exptrTyList = exptrPrsnlRptService.getExptrTyList(exptrSmList);
            model.addAttribute("exptrTyList", exptrTyList);
            // 항목별 집계
            Map<String, Integer> exptrTySmMap = exptrPrsnlRptService.getExptrRptTySmMap(exptrSmList, exptrTyList);
            if (exptrTySmMap != null) {
                model.addAttribute("totSm", exptrTySmMap.get("totSm"));
                // 임시로 되게만 만들자.. 나중에 손보기
                List<Integer> exptyTySmList = new ArrayList<>();
                for (String exptrTyNm : exptrTyList) {
                    for (String key : exptrTySmMap.keySet()) {
                        if (exptrTyNm.equals(key)) exptyTySmList.add(exptrTySmMap.get(key));
                    }
                }
                model.addAttribute("exptyTySmList", exptyTySmList);
            }
            model.addAttribute("statsYy", Integer.toString((Integer) searchParamMap.get("yy")));
            model.addAttribute("statsMnth", Integer.toString((Integer) searchParamMap.get("mnth")));

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.ADMIN_MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/prsnl/rpt/exptr_prsnl_rpt";
    }

    /**
     * 경비 관리 > 경비지출누적집계 > 년도별 경비 지출 엑셀 다운로드
     * 관리자MNGR만 접근 가능
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