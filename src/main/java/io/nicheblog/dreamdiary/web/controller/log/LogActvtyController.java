package io.nicheblog.dreamdiary.web.controller.log;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.log.LogActvtyDto;
import io.nicheblog.dreamdiary.web.model.log.LogActvtySearchParam;
import io.nicheblog.dreamdiary.web.service.log.LogActvtyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * LogActvtyController
 * <pre>
 *  로그 관리 > 활동 로그 관리 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class LogActvtyController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.LOG_ACTVTY_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.LOG_ACTVTY;        // 작업 카테고리 (로그 적재용)

    private final LogActvtyService logActvtyService;

    /**
     * 활동 로그 목록 (전체) 화면 조회
     * 관리자MNGR만 접근 가능
     */
    @GetMapping(Url.LOG_ACTVTY_LIST)
    @Secured(Constant.ROLE_MNGR)
    public String logActvtyList(
            @ModelAttribute("searchParam") LogActvtySearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws IOException {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.LOG_ACTVTY.setAcsPageInfo(Constant.PAGE_LIST));

        // 활동 로그 목록 조회
        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            String baseUrl = Url.LOG_ACTVTY_LIST;
            searchParam = (LogActvtySearchParam) CmmUtils.Param.checkPrevSearchParam(baseUrl, searchParam);
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, "logDt", model);
            // 목록 조회
            Page<LogActvtyDto.LIST> logActvtyList = logActvtyService.getPageDto(searchParam, pageRequest);
            model.addAttribute("logActvtyList", logActvtyList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(logActvtyList));
            // 목록 검색 URL + 파라미터 모델에 추가
            CmmUtils.Param.setModelAttrMap(searchParam, baseUrl, model);

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

        return "/view/log/actvty/log_actvty_list";
    }

    /**
     * 활동 로그 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.LOG_ACTVTY_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> logActvtyDtlAjax(
            final @RequestParam("logActvtyNo") Integer logActvtyNo,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상세 조회 및 응답에 추가
            LogActvtyDto.DTL rsDto = logActvtyService.getDtlDto(logActvtyNo);
            ajaxResponse.setRsltObj(rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + logActvtyNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 활동 로그 > 활동 로그 목록 (전체) 엑셀 다운로드
     * 관리자MNGR만 접근 가능
     */
    // @GetMapping(Url.LOG_ACTVTY_LIST_XLSX_DOWNLOAD)
    // @Secured(Constant.ROLE_MNGR)
    // public void logActvtyListXlsxDownload(
    //         @ModelAttribute("searchParam") LogActvtySearchParam searchParam,
    //         final LogActvtyParam logParam,
    //         final @RequestParam Map<String, Object> searchParamMap
    // ) throws Exception {
//
    //     boolean isSuccess = false;
    //     String rsltMsg = "";
    //     try {
    //         List<Object> logActvtyListXlsx = logActvtyService.logActvtyListXlsx(searchParamMap);
    //         xlsxUtils.listXlxsDownload(Constant.LOG_ACTVTY, logActvtyListXlsx);
    //         isSuccess = true;
    //         rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
    //     } catch (Exception e) {
    //         isSuccess = false;
    //         rsltMsg = MessageUtils.getExceptionMsg(e);
    //         logParam.setExceptionInfo(e);
    //         MessageUtils.alertMessage(rsltMsg, baseUrl);
    //     } finally {
    //         // 로그 관련 처리
    //         logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
    //         publisher.publishEvent(new LogActvtyEvent(this, logParam));
    //     }
    // }
}
