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
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.papr.VcatnSchdulMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.user.UserListDto;
import io.nicheblog.dreamdiary.web.model.vcatn.dy.VcatnDyDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnSchdulDto;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import io.nicheblog.dreamdiary.web.service.vcatn.dy.VcatnDyService;
import io.nicheblog.dreamdiary.web.service.vcatn.papr.VcatnPaprService;
import io.nicheblog.dreamdiary.web.service.vcatn.stats.VcatnStatsYyService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
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
import java.security.InvalidParameterException;
import java.util.List;

/**
 * VcatnDyController
 * <pre>
 *  휴가관리 > 휴가사용일자 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class VcatnDyController
        extends BaseControllerImpl {

    // 작업 카테고리 (로그 적재용)
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.VCATN_DY;      // 작업 카테고리 (로그 적재용)

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return actvtyCtgr.name();
    }

    private final VcatnSchdulMapstruct vcatnSchdulMapstruct = VcatnSchdulMapstruct.INSTANCE;

    @Resource(name = "vcatnPaprService")
    private VcatnPaprService vcatnPaprService;

    @Resource(name = "vcatnStatsYyService")
    private VcatnStatsYyService vcatnStatsYyService;

    @Resource(name = "vcatnDyService")
    private VcatnDyService vcatnDyService;

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
    @GetMapping(SiteUrl.VCATN_DY_LIST)
    @Secured(Constant.ROLE_MNGR)
    public String vcatnDyList(
            final @RequestParam("statsYy") @Nullable String yyStrParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.VCATN_DY.setAcsPageInfo(Constant.PAGE_CAL));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            VcatnStatsYyDto statsYy = null;
            String yyStr = yyStrParam;
            if (StringUtils.isEmpty(yyStrParam)) {
                statsYy = vcatnStatsYyService.getCurrVcatnYyDt();
                yyStr = statsYy.getStatsYy();
            }
            if (statsYy == null) statsYy = vcatnStatsYyService.getVcatnYyDtDto(yyStr);
            // 휴가계획서 최저년도~올해년도 목록 조회
            model.addAttribute("yyList", vcatnPaprService.getVcatnYyList());
            // 휴가계획서 년도 정보 조회 (시작일자~종료일자)
            model.addAttribute("vcatnYy", statsYy);
            List<UserListDto> crtdUserList = userService.getCrdtUserList(statsYy.getBgnDt(), statsYy.getEndDt())
                                                        .getContent();
            model.addAttribute("crtdUserList", crtdUserList);
            // 일반 휴가(날짜범위)를 하루하루로 다 쪼개야 한다.
            Page<VcatnDyDto> vcatnDyPage = vcatnDyService.getVcatnDyList(statsYy);
            List<VcatnDyDto> vcatnDyList = vcatnDyPage == null ? null : vcatnDyPage.getContent();
            model.addAttribute("vcatnDyList", vcatnDyList);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            cdService.setModelCdData(Constant.VCATN_CD, model);
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

        return "/view/vcatn/dy/vcatn_dy_list";
    }

    /**
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 등록
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {SiteUrl.VCATN_DY_REG_AJAX, SiteUrl.VCATN_DY_MDF_AJAX})
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnDyRegAjax(
            final @Valid VcatnSchdulDto vcatnSchdulDto,
            final Integer vcatnSchdulNo,
            final LogActvtyParam logParam,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = (vcatnSchdulNo == null);
            VcatnSchdulDto result = isReg ? vcatnDyService.regist(vcatnSchdulDto) : vcatnDyService.modify(vcatnSchdulDto, vcatnSchdulNo);
            isSuccess = (result.getVcatnSchdulNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(vcatnSchdulDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 단일 조회 (ajax)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(value = SiteUrl.VCATN_DY_DTL_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnDyDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("vcatnSchdulNo") String vcatnSchdulNoStr
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer vcatnSchdulNo = Integer.parseInt(vcatnSchdulNoStr);
            VcatnSchdulDto resultObj = vcatnDyService.getDtlDto(vcatnSchdulNo);
            ajaxResponse.setResultObj(resultObj);
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
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 삭제 (ajax)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(SiteUrl.VCATN_DY_DEL_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnDyDelAjax(
            final LogActvtyParam logParam,
            final @RequestParam("vcatnSchdulNo") String vcatnSchdulNoStr
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer vcatnSchdulNo = Integer.parseInt(vcatnSchdulNoStr);
            isSuccess = vcatnDyService.delete(vcatnSchdulNo);
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
     * 휴가 관리 > 휴가사용일자 > 휴가사용일자 엑셀 다운로드
     * 관리자MNGR만 접근 가능
     */
    // @RequestMapping(SiteUrl.VCATN_DY_XLSX_DOWNLOAD)
    // @Secured(Constant.ROLE_MNGR)
    // public void vcatnDyXlsxDownload(
    //         final LogActvtyParam logParam,
    //         final @RequestParam("yy") @Nullable String yyStr
    // ) throws Exception {
//
    //     boolean isSuccess = false;
    //     String resultMsg = "";
    //     try {
    //         VcatnStatsYyDto statsYy = vcatnStatsYyService.getVcatnYyDtDto(yyStr);
    //         // 일반 휴가(날짜범위)를 하루하루로 다 쪼개야 한다.
    //         Page<VcatnDyDto> vcatnDyList = vcatnDyService.getVcatnDyList(statsYy);
    //         List<Object> statsObjList = new ArrayList<>();
    //         for (VcatnDyDto dy : vcatnDyList) {
    //             statsObjList.add(vcatnSchdulMapstruct.toDyXlsxDto(dy));
    //         }
    //         xlsxUtils.listXlxsDownload(Constant.VCATN_DY, statsObjList);
    //         isSuccess = true;
    //         resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
    //     } catch (Exception e) {
    //         isSuccess = false;
    //         resultMsg = MessageUtils.getExceptionMsg(e);
    //         logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
    //         MessageUtils.alertMessage(resultMsg, SiteUrl.VCATN_DY_LIST);
    //     } finally {
    //         // 로그 관련 처리
    //         logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
    //         publisher.publishEvent(new LogActvtyEvent(this, logParam));
    //     }
    // }

}
