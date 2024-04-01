package io.nicheblog.dreamdiary.web.controller.log;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.log.LogActvtyDto;
import io.nicheblog.dreamdiary.web.model.log.LogActvtySearchParam;
import io.nicheblog.dreamdiary.web.service.log.LogActvtyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

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
@Log4j2
public class LogActvtyController
        extends BaseControllerImpl {

    private final String baseUrl = SiteUrl.LOG_ACTVTY_LIST;
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.LOG_ACTVTY;        // 작업 카테고리 (로그 적재용)

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return actvtyCtgr.name();
    }

    // @Resource(name = "xlsxUtils")
    // private XlsxUtils xlsxUtils;

    @Resource(name = "logActvtyService")
    private LogActvtyService logActvtyService;

    /**
     * 활동 로그 목록 (전체) 화면 조회
     * 관리자MNGR만 접근 가능
     */
    @GetMapping(SiteUrl.LOG_ACTVTY_LIST)
    @Secured(Constant.ROLE_MNGR)
    public String logActvtyList(
            final @ModelAttribute("searchParam") LogActvtySearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws IOException {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.LOG_ACTVTY.setAcsPageInfo(Constant.PAGE_LIST));

        // 활동 로그 목록 조회
        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            Map<String, Object> listParamMap = CmmUtils.Param.checkPrevSearchMap(searchParamMap, baseUrl, searchParam);

            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, "logDt", model);
            Page<LogActvtyDto> logActvtyList = logActvtyService.getListDto(listParamMap, pageRequest);
            if (logActvtyList != null) model.addAttribute("logActvtyList", logActvtyList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(logActvtyList));
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);

            // 검색 파라미터 다시 모델에 추가
            CmmUtils.Param.setModelAttrMap(listParamMap, searchParam, baseUrl, model);
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

        return "/view/log/actvty/log_actvty_list";
    }

    /**
     * 활동 로그 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.LOG_ACTVTY_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> logActvtyDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("logActvtyNo") Integer logActvtyNo
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            LogActvtyDto rsDto = logActvtyService.getDtlDto(logActvtyNo);
            ajaxResponse.setResultObj(rsDto);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + logActvtyNo);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 활동 로그 > 활동 로그 목록 (전체) 엑셀 다운로드
     * 관리자MNGR만 접근 가능
     */
    // @RequestMapping(SiteUrl.LOG_ACTVTY_LIST_XLSX_DOWNLOAD)
    // @Secured(Constant.ROLE_MNGR)
    // public void logActvtyListXlsxDownload(
    //         final LogActvtyParam logParam,
    //         final @ModelAttribute("searchParam") LogActvtySearchParam searchParam,
    //         final @RequestParam Map<String, Object> searchParamMap
    // ) throws Exception {
//
    //     boolean isSuccess = false;
    //     String resultMsg = "";
    //     try {
    //         List<Object> logActvtyListXlsx = logActvtyService.logActvtyListXlsx(searchParamMap);
    //         xlsxUtils.listXlxsDownload(Constant.LOG_ACTVTY, logActvtyListXlsx);
    //         isSuccess = true;
    //         resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
    //     } catch (Exception e) {
    //         isSuccess = false;
    //         resultMsg = MessageUtils.getExceptionMsg(e);
    //         logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
    //         MessageUtils.alertMessage(resultMsg, baseUrl);
    //     } finally {
    //         // 로그 관련 처리
    //         logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
    //         publisher.publishEvent(new LogActvtyEvent(this, logParam));
    //     }
    // }
}
