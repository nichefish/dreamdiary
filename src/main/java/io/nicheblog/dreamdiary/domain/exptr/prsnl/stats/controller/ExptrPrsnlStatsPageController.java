package io.nicheblog.dreamdiary.domain.exptr.prsnl.stats.controller;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.service.ExptrPrsnlPaprService;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.stats.model.ExptrPrsnlStatsDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.stats.service.ExptrPrsnlStatsService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.domain.admin.menu.SiteMenu;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nullable;
import java.util.List;

/**
 * ExptrPrsnlController
 * <pre>
 *  경비 관리 > 경비지출누적집계 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class ExptrPrsnlStatsPageController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.EXPTR_PRSNL_STATS_PAGE;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_PRSNL_STATS;        // 작업 카테고리 (로그 적재용)

    private final ExptrPrsnlPaprService exptrPrsnlPaprService;
    private final ExptrPrsnlStatsService exptrPrsnlStatsService;

    /**
     * 경비 관리 > 경비지출누적집계 > 년도별 경비지출누적집계 화면 조회
     * (관리자MNGR만 접근 가능.)
     *
     * @param yyStrParam 통계 년도
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.EXPTR_PRSNL_STATS_PAGE)
    @Secured(Constant.ROLE_MNGR)
    public String exptrPrsnlStats(
            final @RequestParam("statsYy") @Nullable String yyStrParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_STATS.setAcsPageInfo(Constant.PAGE_STATS));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 초기 년도검색 파라미터 세팅
            final String yyStr = !StringUtils.isEmpty(yyStrParam) ? yyStrParam : DateUtils.getCurrYyStr();
            // 경비지출서 최저년도~올해년도 목록 조회
            model.addAttribute("yyList", exptrPrsnlPaprService.getExptrPrsnlYyList());
            // 올해년도에 근무이력이 있는(중도퇴사 포함) 모든 직원(재직+프리랜서) 전원에 대하여 산정
            List<ExptrPrsnlStatsDto> statsList = exptrPrsnlStatsService.getExptrPrsnlStatsList(yyStr);
            model.addAttribute("statsList", statsList);
            model.addAttribute("statsYy", yyStr);

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

        return "/view/exptr/prsnl/stats/exptr_prsnl_stats_page";
    }

    /**
     * 경비 관리 > 경비지출누적집계 > 경비지출서 상세 화면 조회 (관리자)
     * (관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.EXPTR_PRSNL_STATS_DTL)
    @Secured(Constant.ROLE_MNGR)
    public String exptrPrsnlStatsDtl(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_STATS.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            final ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            exptrPrsnlPaprService.hitCntUp(key);
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.EXPTR_PRSNL_STATS_PAGE);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/prsnl/stats/exptr_prsnl_stats_dtl";
    }
}