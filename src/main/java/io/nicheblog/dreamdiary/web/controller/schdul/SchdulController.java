package io.nicheblog.dreamdiary.web.controller.schdul;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulCalDto;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulDto;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulSearchParam;
import io.nicheblog.dreamdiary.web.model.user.UserListDto;
import io.nicheblog.dreamdiary.web.service.cmm.NotifyService;
import io.nicheblog.dreamdiary.web.service.schdul.SchdulCalService;
import io.nicheblog.dreamdiary.web.service.schdul.SchdulService;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * SchdulController
 * <pre>
 *  일정 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class SchdulController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.SCHDUL_CAL;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.SCHDUL;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "schdulService")
    private SchdulService schdulService;
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
    @GetMapping(SiteUrl.SCHDUL_CAL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String schdulCal(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.SCHDUL_CAL.setAcsPageInfo(Constant.PAGE_CAL));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            List<UserListDto> crtdUserList = userService.getCrdtUserList(DateUtils.getCurrDateAddDayStr(-40), DateUtils.getCurrDateAddDayStr(40))
                                                        .getContent();
            model.addAttribute("crtdUserList", crtdUserList);
            cdService.setModelCdData(Constant.SCHDUL_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/schdul/schdul_cal";
    }

    /**
     * 일정 > 전체 일정 (달력) 목록 데이터 조회 (ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.SCHDUL_CAL_LIST_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulCalListAjax(
            final LogActvtyParam logParam,
            final SchdulSearchParam searchParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            List<SchdulCalDto> schdulCalList = schdulCalService.getSchdulTotalCalList(searchParam);
            ajaxResponse.setResultList(schdulCalList);

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
     * 일정 > 전체일정 > 일정 등록(ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {SiteUrl.SCHDUL_REG_AJAX, SiteUrl.SCHDUL_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulRegAjax(
            final @Valid SchdulDto schdulDto,
            final LogActvtyParam logParam,
            final @RequestParam("postNo") @Nullable Integer postNo,
            final @RequestParam("jandiYn") @Nullable String jandiYn,
            final @RequestParam("trgetTopic") @Nullable String trgetTopic,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            boolean isReg = postNo != null;
            SchdulDto result = isReg ? schdulService.regist(schdulDto) : schdulService.modify(schdulDto, postNo);
            isSuccess = (result.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            // 잔디 메세지 발송 :: 메인 로직과 분리
            // if (isSuccess && "Y".equals(jandiYn)) {
            //     String jandiResultMsg = notifyService.notifySchdulReg(trgetTopic, result, logParam);
            //     resultMsg = resultMsg + "\n" + jandiResultMsg;
            // }
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(schdulDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 일정 > 전체일정 > 일정 조회 (ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.SCHDUL_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("schdulNo") Integer postNo
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            SchdulDto schdul = schdulService.getDtlDto(postNo);
            ajaxResponse.setResultObj(schdul);
            isSuccess = (schdul.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + postNo);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 일정 > 전체일정 > 일정 삭제 (ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.SCHDUL_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulDelAjax(
            final LogActvtyParam logParam,
            final @RequestParam("schdulNo") String schdulNoStr
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer schdulNo = Integer.parseInt(schdulNoStr);

            isSuccess = schdulService.delete(schdulNo);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + schdulNoStr);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
