package io.nicheblog.dreamdiary.web.controller.flsys;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.CookieUtils;
import io.nicheblog.dreamdiary.global.util.FileUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.flsys.FlsysCmmDto;
import io.nicheblog.dreamdiary.web.model.flsys.FlsysSearchParam;
import io.nicheblog.dreamdiary.web.service.flsys.FlsysService;
import lombok.Getter;
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

/**
 * FlsysController
 * <pre>
 *  파일시스템 컨트롤러
 * </pre>
 * TODO: 보완 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class FlsysController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.FLSYS_HOME;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.FLSYS;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "flsysService")
    public FlsysService flsysService;

    /**
     * 파일시스템 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.FLSYS_HOME)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String flsysList(
            @ModelAttribute("searchParam") FlsysSearchParam searchParam,
            final @RequestParam("filePath") @Nullable String filePathParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        model.addAttribute(Constant.SITE_MENU, SiteMenu.LGN_POLICY.setAcsPageInfo("HOME"));

        // 활동 로그 목록 조회
        boolean isSuccess = false;
        String resultMsg = "";
        try {
            String filePath = !StringUtils.isEmpty(filePathParam) ? filePathParam : Constant.HOME_FLSYS;
            FlsysCmmDto file = flsysService.getFlsysByPath(filePath);
            model.addAttribute("file", file);

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
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

        return "/view/flsys/flsys_home";
    }

    /**
     * 파일시스템 정보 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = SiteUrl.FLSYS_LIST_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> flsysListAjax(
            final @RequestParam("filePath") String filePath,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (!filePath.startsWith(Constant.HOME_FLSYS)) throw new IllegalArgumentException("허용되지 않은 경로입니다.");
            FlsysCmmDto file = flsysService.getFlsysByPath(filePath);
            ajaxResponse.setResultObj(file);

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 파일 시스템 파일 다운로드
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.FLSYS_FILE_DOWNLOAD)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public void flsysFileDownload(
            final @RequestParam("filePath") String filePath,
            final LogActvtyParam logParam
    ) throws Exception {

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (!filePath.startsWith(Constant.HOME_FLSYS)) throw new IllegalArgumentException("허용되지 않은 경로입니다.");

            FlsysCmmDto file = flsysService.getFlsysByPath(filePath);
            // 응답 헤더 설정 및 한글 파일명 처리 (메소드 분리)
            CookieUtils.setFileDownloadSuccessCookie();
            FileUtils.downloadFile(file.getFile());

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }
}
