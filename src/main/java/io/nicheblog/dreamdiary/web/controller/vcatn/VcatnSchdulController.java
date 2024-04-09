package io.nicheblog.dreamdiary.web.controller.vcatn;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.vcatn.schdul.VcatnSchdulDto;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import io.nicheblog.dreamdiary.web.service.vcatn.papr.VcatnPaprService;
import io.nicheblog.dreamdiary.web.service.vcatn.schdul.VcatnSchdulService;
import io.nicheblog.dreamdiary.web.service.vcatn.stats.VcatnStatsYyService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * VcatnSchdulController
 * <pre>
 *  휴가관리 > 휴가사용일자 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class VcatnSchdulController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.VCATN_SCHDUL_LIST;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.VCATN_SCHDUL;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "vcatnPaprService")
    private VcatnPaprService vcatnPaprService;
    @Resource(name = "vcatnStatsYyService")
    private VcatnStatsYyService vcatnStatsYyService;
    @Resource(name = "vcatnSchdulService")
    private VcatnSchdulService vcatnSchdulService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "cdService")
    private CdService cdService;

    // @Resource(name = "xlsxUtils")
    // private XlsxUtils xlsxUtils;

    /**
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 목록 화면 조회
     * 관리자MNGR만 접근 가능
     */
    @GetMapping(SiteUrl.VCATN_SCHDUL_LIST)
    @Secured(Constant.ROLE_MNGR)
    public String vcatnSchdulList(
            final @RequestParam("statsYy") @Nullable String yyStrParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.VCATN_SCHDUL.setAcsPageInfo(Constant.PAGE_CAL));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 휴가계획서 년도 정보 조회 (시작일자~종료일자 세팅 정보)
            VcatnStatsYyDto statsYy = null;
            String yyStr = yyStrParam;
            if (StringUtils.isEmpty(yyStrParam)) {
                statsYy = vcatnStatsYyService.getCurrVcatnYyDt();
                yyStr = statsYy.getStatsYy();
            }
            if (statsYy == null) statsYy = vcatnStatsYyService.getVcatnYyDtDto(yyStr);
            model.addAttribute("vcatnYy", statsYy);
            // 휴가계획서 최저년도~올해 년도(year) 목록 조회
            model.addAttribute("yyList", vcatnPaprService.getVcatnYyList());
            // 직원 목록 조회 (등록에 쓰임)
            List<UserDto.LIST> crtdUserList = userService.getCrdtUserList(statsYy.getBgnDt(), statsYy.getEndDt());
            model.addAttribute("crtdUserList", crtdUserList);
            // 일반 휴가(날짜범위)를 하루하루로 다 쪼개야 한다.
            model.addAttribute("vcatnSchdulList", vcatnSchdulService.getListDto(statsYy));
            cdService.setModelCdData(Constant.VCATN_CD, model);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, SiteUrl.ADMIN_MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/vcatn/schdul/vcatn_schdul_list";
    }

    /**
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 등록
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {SiteUrl.VCATN_SCHDUL_REG_AJAX, SiteUrl.VCATN_SCHDUL_MDF_AJAX})
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnSchdulRegAjax(
            final @Valid VcatnSchdulDto vcatnSchdul,
            final Integer vcatnSchdulNo,
            final LogActvtyParam logParam,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // Validation
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            // 등록/수정 처리
            boolean isReg = (vcatnSchdulNo == null);
            VcatnSchdulDto result = isReg ? vcatnSchdulService.regist(vcatnSchdul) : vcatnSchdulService.modify(vcatnSchdul, vcatnSchdulNo);

            isSuccess = (result.getVcatnSchdulNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(vcatnSchdul.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 단일 조회 (ajax)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(value = SiteUrl.VCATN_SCHDUL_DTL_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnSchdulDtlAjax(
            final @RequestParam("vcatnSchdulNo") Integer vcatnSchdulNo,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 세팅
            VcatnSchdulDto resultObj = vcatnSchdulService.getDtlDto(vcatnSchdulNo);
            ajaxResponse.setResultObj(resultObj);

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

    /**
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 삭제 (ajax)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(SiteUrl.VCATN_SCHDUL_DEL_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnSchdulDelAjax(
            final @RequestParam("vcatnSchdulNo") Integer vcatnSchdulNo,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제 처리
            isSuccess = vcatnSchdulService.delete(vcatnSchdulNo);
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

    /**
     * 휴가 관리 > 휴가사용일자 > 휴가사용일자 엑셀 다운로드
     * 관리자MNGR만 접근 가능
     *//*
    // @RequestMapping(SiteUrl.VCATN_SCHDUL_XLSX_DOWNLOAD)
    // @Secured(Constant.ROLE_MNGR)
    // public void vcatnSchdulXlsxDownload(
    //         final LogActvtyParam logParam,
    //         final @RequestParam("yy") @Nullable String yyStr
    // ) throws Exception {
//
    //     boolean isSuccess = false;
    //     String rsltMsg = "";
    //     try {
    //         VcatnStatsYyDto statsYy = vcatnStatsYyService.getVcatnYyDtDto(yyStr);
    //         // 일반 휴가(날짜범위)를 하루하루로 다 쪼개야 한다.
    //         Page<VcatnSchdulDto> vcatnSchdulList = vcatnSchdulService.getVcatnSchdulList(statsYy);
    //         List<Object> statsObjList = new ArrayList<>();
    //         for (VcatnSchdulDto dy : vcatnSchdulList) {
    //             statsObjList.add(vcatnSchdulMapstruct.toDyXlsxDto(dy));
    //         }
    //         xlsxUtils.listXlxsDownload(Constant.VCATN_SCHDUL, statsObjList);
    //         isSuccess = true;
    //         rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
    //     } catch (Exception e) {
    //         isSuccess = false;
    //         rsltMsg = MessageUtils.getExceptionMsg(e);
    //         logParam.setExceptionInfo(e);
    //         MessageUtils.alertMessage(rsltMsg, SiteUrl.VCATN_SCHDUL_LIST);
    //     } finally {
    //         // 로그 관련 처리
    //         logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
    //         publisher.publishEvent(new LogActvtyEvent(this, logParam));
    //     }
    // }

*/
}