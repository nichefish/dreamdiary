package io.nicheblog.dreamdiary.web.controller.exptr.prsnl.stats;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.stats.ExptrPrsnlStatsDto;
import io.nicheblog.dreamdiary.web.service.exptr.prsnl.papr.ExptrPrsnlPaprService;
import io.nicheblog.dreamdiary.web.service.exptr.prsnl.stats.ExptrPrsnlStatsService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;

/**
 * ExptrPrsnlController
 * <pre>
 *  경비 관리 > 경비지출누적집계 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class ExptrPrsnlStatsController
        extends BaseControllerImpl {

    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_PRSNL_STATS;      // 작업 카테고리 (로그 적재용)

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return actvtyCtgr.name();
    }

    @Resource(name = "exptrPrsnlService")
    private ExptrPrsnlPaprService exptrPrsnlPaprService;

    @Resource(name = "exptrPrsnlStatsService")
    private ExptrPrsnlStatsService exptrPrsnlStatsService;

    // @Resource(name = "boardPostViewerService")
    // private BoardPostViewerService boardPostViewerService;

    // @Resource(name = "xlsxUtils")
    // private XlsxUtils xlsxUtils;

    /**
     * 경비 관리 > 경비지출누적집계 > 년도별 경비지출누적집계 화면 조회
     * 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.EXPTR_PRSNL_STATS_PAGE)
    @Secured(Constant.ROLE_MNGR)
    public String exptrPrsnlStats(
            final LogActvtyParam logParam,
            final @RequestParam("statsYy") @Nullable String yyStrParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_STATS.setAcsPageInfo(Constant.PAGE_STATS));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            String yyStr = !StringUtils.isEmpty(yyStrParam) ? yyStrParam : DateUtils.getCurrYearStr();
            // 경비지출서 최저년도~올해년도 목록 조회
            model.addAttribute("yyList", exptrPrsnlPaprService.getExptrPrsnlYyList());
            // 올해년도에 근무이력이 있는(중도퇴사 포함) 모든 신지넷+빅스소프트 직원(재직+프리랜서) 전원에 대하여 산정
            List<ExptrPrsnlStatsDto> statsList = exptrPrsnlStatsService.getExptrPrsnlStatsList(yyStr);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            model.addAttribute("statsList", statsList);
            model.addAttribute("statsYy", yyStr);
            // 관리자페이지 화면 모드 세팅
            session.setAttribute("userMode", Constant.AUTH_MNGR);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, SiteUrl.ADMIN_MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/prsnl/stats/exptr_prsnl_stats_page";
    }

    /**
     * 경비 관리 > 경비지출누적집계 > 경비지출서 상세 화면 조회 (관리자)
     * 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.EXPTR_PRSNL_STATS_DTL)
    @Secured(Constant.ROLE_MNGR)
    public String exptrPrsnlStatsDtl(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_STATS.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 열람자 목록 및 조회수 카운트 추가
            // try {
            //     List<BoardPostViewerDto> viewerList = rsDto.getViewerList();
            //     if (!boardPostViewerService.hasAlreadyView(viewerList)) {
            //         BoardPostViewerDto dto = boardPostViewerService.regPostViewer(postKey);
            //         rsDto.addPostViewer(dto);
            //     }
            //     exptrPrsnlService.hitCntUp(postKey);
            // } catch (Exception e) {
            //     resultMsg = MessageUtils.getExceptionMsg(e);
            //     logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            //     publisher.publishEvent(new LogActvtyEvent(this, logParam));
            // }
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 관리자페이지 화면 모드 세팅
            session.setAttribute("userMode", Constant.AUTH_MNGR);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, SiteUrl.EXPTR_PRSNL_STATS_PAGE);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/prsnl/stats/exptr_prsnl_stats_dtl";
    }

    /**
     * 경비 관리 > 경비지출누적집계 > 경비지출서 취합완료 처리 (Ajax)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(SiteUrl.EXPTR_PRSNL_STATS_CF_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrPrsnlStatsComptAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = exptrPrsnlStatsService.exptrPrsnlStatsCompt(key);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 경비지출누적집계 > 년도별 경비 지출 엑셀 다운로드
     * 관리자MNGR만 접근 가능
     */
    // @RequestMapping(SiteUrl.EXPTR_PRSNL_STATS_XLSX_DOWNLOAD)
    // @Secured(Constant.ROLE_MNGR)
    // public void exptrPrsnlStatsXlsxDownload(
    //         final LogActvtyParam logParam,
    //         final @RequestParam("statsYy") @Nullable String yyStr
    // ) throws Exception {
//
    //     boolean isSuccess = false;
    //     String resultMsg = "";
    //     try {
    //         // 올해년도에 근무이력이 있는(중도퇴사 포함) 모든 신지넷+빅스소프트 직원(재직+프리랜서) 전원에 대하여 산정
    //         List<Object> statsObjList = exptrPrsnlStatsService.getExptrPrsnlStatsListXlsx(yyStr);
    //         xlsxUtils.listXlxsDownload(Constant.EXPTR_PRSNL_PAPR, statsObjList);
    //         isSuccess = true;
    //         resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
    //     } catch (Exception e) {
    //         isSuccess = false;
    //         resultMsg = MessageUtils.getExceptionMsg(e);
    //         logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
    //         MessageUtils.alertMessage(resultMsg, SiteUrl.EXPTR_PRSNL_STATS_PAGE);
    //     } finally {
    //         // 로그 관련 처리
    //         logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
    //         publisher.publishEvent(new LogActvtyEvent(this, logParam));
    //     }
    // }
}