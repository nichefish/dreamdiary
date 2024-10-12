package io.nicheblog.dreamdiary.domain.vcatn.stats.controller;

import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.domain.vcatn.papr.service.VcatnPaprService;
import io.nicheblog.dreamdiary.domain.vcatn.stats.model.VcatnStatsTotalDto;
import io.nicheblog.dreamdiary.domain.vcatn.stats.model.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.domain.vcatn.stats.service.VcatnStatsService;
import io.nicheblog.dreamdiary.domain.vcatn.stats.service.VcatnStatsYyService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.SiteMenu;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * VcatnStatsController
 * <pre>
 *  휴가관리 > 년도별 휴가관리 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class VcatnStatsYyController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.VCATN_STATS_YY;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.VCATN_STATS;      // 작업 카테고리 (로그 적재용)

    private final VcatnPaprService vcatnPaprService;
    private final VcatnStatsService vcatnStatsService;
    private final VcatnStatsYyService vcatnStatsYyService;

    /**
     * 휴가관리 > 년도별 휴가관리 > 목록 화면 조회
     * (관리자MNGR만 접근 가능.)
     *
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.VCATN_STATS_YY)
    @Secured(Constant.ROLE_MNGR)
    public String vcatnStatsList(
            final @RequestParam("statsYy") @Nullable String yyStrParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws IOException {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.VCATN_STATS.setAcsPageInfo(Constant.PAGE_CAL));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 휴가계획서 년도 정보 조회 (시작일자~종료일자 정보)
            VcatnStatsYyDto statsYy = null;
            String yyStr = yyStrParam;
            if (StringUtils.isEmpty(yyStrParam)) {
                statsYy = vcatnStatsYyService.getCurrVcatnYyDt();
                yyStr = statsYy.getStatsYy();
            }
            if (statsYy == null) statsYy = vcatnStatsYyService.getVcatnYyDtDto(yyStr);
            model.addAttribute("vcatnYy", statsYy);
            // 휴가계획서 최저년도~올해 년도 (year) 목록 조회
            model.addAttribute("yyList", vcatnPaprService.getVcatnYyList());
            // 해당년도에 근무이력이 있는(중도퇴사 포함) 모든 직원(재직+프리랜서) 전원에 대하여 산정
            // List<VcatnStatsDto> statsList = vcatnStatsServicea.getVcatnStatsList(statsYy);
            // model.addAttribute("statsList", statsList);

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

        return "/view/vcatn/stats/vcatn_stats";
    }

    /**
     * 휴가관리 > 년도별 휴가관리 > 업데이트 (Ajax)
     * (관리자MNGR만 접근 가능.)
     */
    @PostMapping(Url.VCATN_STATS_YY_UPDT_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnStatsUpdtAjax(
            final VcatnStatsTotalDto vcatnStatsTotal,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 휴가사용현황 정보 저장
            isSuccess = vcatnStatsService.regStatsTotal(vcatnStatsTotal);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(vcatnStatsTotal.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 휴가관리 > 년도별 휴가관리 > 목록 엑셀 다운로드
     * (관리자MNGR만 접근 가능.)
     */
    // @GetMapping(Url.VCATN_STATS_YY_XLSX_DOWNLOAD)
    // @Secured(Constant.ROLE_MNGR)
    // public void vcatnStatsXlsxDownload(
    //         final LogActvtyParam logParam,
    //         final @RequestParam("statsYy") String yyStr
    // ) throws Exception {
//
    //     boolean isSuccess = false;
    //     String rsltMsg = "";
    //     try {
    //         VcatnStatsYyDto statsYy = StringUtils.isNotEmpty(yyStr) ? vcatnStatsYyService.getVcatnYyDtDto(yyStr) : vcatnStatsYyService.getCurrVcatnYyDt();
    //         // 해당년도에 근무이력이 있는(중도퇴사 포함) 모든 직원(재직+프리랜서) 전원에 대하여 산정
    //         List<Object> statsObjList = vcatnStatsService.getVcatnStatsListXlsx(statsYy);
    //         xlsxUtils.listXlxsDownload(Constant.VCATN_STATS, statsObjList);
    //         isSuccess = true;
    //         rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
    //     } catch (Exception e) {
    //         isSuccess = false;
    //         rsltMsg = MessageUtils.getExceptionMsg(e);
    //         logParam.setExceptionInfo(e);
    //         MessageUtils.alertMessage(rsltMsg, Url.VCATN_STATS_YY);
    //     } finally {
    //         // 로그 관련 처리
    //         logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
    //         publisher.publishEvent(new LogActvtyEvent(this, logParam));
    //     }
    // }
}
