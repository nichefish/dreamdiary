package io.nicheblog.dreamdiary.web.controller.vcatn;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.CmmUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprListDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprParam;
import io.nicheblog.dreamdiary.web.service.cmm.NotifyService;
import io.nicheblog.dreamdiary.web.service.vcatn.papr.VcatnPaprService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.Map;

/**
 * VcatnPaprController
 * <pre>
 *  휴가계획서 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class VcatnPaprController
        extends BaseControllerImpl {

    // 작업 카테고리 (로그 적재용)
    private final String baseUrl = SiteUrl.VCATN_PAPR_LIST;
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.VCATN_PAPR;      // 작업 카테고리 (로그 적재용)

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return actvtyCtgr.name();
    }

    @Resource(name = "vcatnPaprService")
    private VcatnPaprService vcatnPaprService;

    @Resource(name = "notifyService")
    private NotifyService notifyService;

    @Resource(name = "cdService")
    private CdService cdService;

    /**
     * 일정  > 휴가 계획서 > 휴가 계획서 목록
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @GetMapping(SiteUrl.VCATN_PAPR_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String vcatnPaprList(
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") VcatnPaprParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.VCATN_PAPR.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            Map<String, Object> listParamMap = CmmUtils.checkPrevSearchMap(searchParamMap, baseUrl, searchParam);

            // 상단 고정 목록 조회
            model.addAttribute("vcatnPaprFxdList", vcatnPaprService.getFxdList());
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, "managt.managtDt", model);
            Page<VcatnPaprListDto> vcatnPaprList = vcatnPaprService.getListDto(listParamMap, pageRequest);
            if (vcatnPaprList != null) model.addAttribute("vcatnPaprList", vcatnPaprList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(vcatnPaprList));
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);

            // 검색 파라미터 다시 모델에 추가
            CmmUtils.setModelAttrMap(listParamMap, searchParam, baseUrl, model);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, SiteUrl.MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return "/view/vcatn/papr/vcatn_papr_list";
    }

    /**
     * 일정  > 휴가 계획서 > 휴가 계획서 등록 폼 이동
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.VCATN_PAPR_REG_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String vcatnPaprRegForm(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.VCATN_PAPR.setAcsPageInfo(Constant.PAGE_REG));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            model.addAttribute("post", new VcatnPaprDto());      // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute(Constant.IS_REG, true);           // 등록/수정 화면 플래그 세팅
            cdService.setModelCdData(Constant.VCATN_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
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

        return "/view/vcatn/papr/vcatn_papr_reg_form";
    }

    /**
     * 일정  > 휴가 계획서 > 휴가 계획서 등록/수정(ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {SiteUrl.VCATN_PAPR_REG_AJAX, SiteUrl.VCATN_PAPR_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnPaprRegAjax(
            final @Valid VcatnPaprDto vcatnPaprDto,
            final @RequestParam("postNo") @Nullable Integer key,
            final LogActvtyParam logParam,
            final @RequestParam("jandiYn") @Nullable String jandiYn,
            final @RequestParam("trgetTopic") @Nullable String trgetTopic,
            final MultipartHttpServletRequest request,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = key == null;
            VcatnPaprDto result = isReg ? vcatnPaprService.regist(vcatnPaprDto, request) : vcatnPaprService.modify(vcatnPaprDto, key, request);
            isSuccess = (result.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            // 조치자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, result.getClsfKey()));
            // 잔디 메세지 발송 :: 메인 로직과 분리
            // if (isSuccess && "Y".equals(jandiYn)) {
            //     String jandiResultMsg = notifyService.notifyVcatnPaprReg(trgetTopic, result, logParam);
            //     resultMsg = resultMsg + "\n" + jandiResultMsg;
            // }
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(vcatnPaprDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 일정  > 휴가 계획서 > 휴가 계획서 확인 처리 (ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(SiteUrl.VCATN_PAPR_CF_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnPaprCfAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            VcatnPaprDto result = vcatnPaprService.vcatnCf(key);
            isSuccess = (result.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
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
     * 일정  > 휴가 계획서 > 휴가 계획서 수정 폼 이동
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.VCATN_PAPR_MDF_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String vcatnPaprMdfForm(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.VCATN_PAPR.setAcsPageInfo(Constant.PAGE_MDF));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            VcatnPaprDto rsVcatnPaprDto = vcatnPaprService.getDtlDto(key);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            model.addAttribute("post", rsVcatnPaprDto);
            model.addAttribute(Constant.IS_MDF, true);
            cdService.setModelCdData(Constant.VCATN_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
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

        return "/view/vcatn/papr/vcatn_papr_reg_form";
    }

    /**
     * 일정  > 휴가 계획서 > 상세화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.VCATN_PAPR_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String vcatnPaprDtl(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.VCATN_PAPR.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            VcatnPaprDto rsDto = vcatnPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 조회수 카운트 추가
            vcatnPaprService.hitCntUp(key);
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
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

        return "/view/vcatn/papr/vcatn_papr_dtl";
    }

    /**
     * 일정  > 휴가 계획서 > 상세 조회 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.VCATN_PAPR_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnPaprDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            VcatnPaprDto rsDto = vcatnPaprService.getDtlDto(key);
            ajaxResponse.setResultObj(rsDto);
            // 조회수 카운트 추가
            vcatnPaprService.hitCntUp(key);
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
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
     * 일정 > 휴가계획서 > 휴가계획서 삭제 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(SiteUrl.VCATN_PAPR_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnPaprDelAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = vcatnPaprService.delete(key);
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
}
