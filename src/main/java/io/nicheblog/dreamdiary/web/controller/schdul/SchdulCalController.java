package io.nicheblog.dreamdiary.web.controller.schdul;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulCalDto;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulSearchParam;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.service.schdul.SchdulCalService;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * SchdulCalController
 * <pre>
 *  일정 달력 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class SchdulCalController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.SCHDUL_CAL;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.SCHDUL;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "schdulCalService")
    private SchdulCalService schdulCalService;
    @Resource(name = "cdService")
    private CdService cdService;
    @Resource(name = "userService")
    private UserService userService;

    /**
     * 일정 > 전체 일정 (달력) 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.SCHDUL_CAL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String schdulCal(
            @ModelAttribute("searchParam") SchdulSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.SCHDUL_CAL.setAcsPageInfo(Constant.PAGE_CAL));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 재직자 목록 조회 및 모델에 추가 :: (일정 등록 참가자용)
            List<UserDto.LIST> crtdUserList = userService.getCrdtUserList(DateUtils.getCurrDateAddDayStr(-40), DateUtils.getCurrDateAddDayStr(40));
            model.addAttribute("crtdUserList", crtdUserList);
            // 코드 데이터 모델에 추가
            cdService.setModelCdData(Constant.SCHDUL_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/schdul/schdul_cal";
    }

    /**
     * 일정 > 전체 일정 (달력) 목록 데이터 조회 (ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.SCHDUL_CAL_LIST_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulCalListAjax(
            final LogActvtyParam logParam,
            final SchdulSearchParam searchParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 목록 조회 및 응답에 추가
            List<SchdulCalDto> schdulCalList = schdulCalService.getSchdulTotalCalList(searchParam);
            ajaxResponse.setRsltList(schdulCalList);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
