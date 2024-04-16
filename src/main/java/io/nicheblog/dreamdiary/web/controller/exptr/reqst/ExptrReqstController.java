package io.nicheblog.dreamdiary.web.controller.exptr.reqst;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstDto;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstSearchParam;
import io.nicheblog.dreamdiary.web.service.cmm.tag.TagService;
import io.nicheblog.dreamdiary.web.service.exptr.reqst.ExptrReqstService;
import lombok.Getter;
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
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * ExptrReqstController
 * <pre>
 *  경비 관리 > 물품구매/경조사비 신청 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class ExptrReqstController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.EXPTR_REQST_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_REQST;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "cdService")
    public CdService cdService;
    @Resource(name = "exptrReqstService")
    public ExptrReqstService exptrReqstService;
    @Resource(name = "tagService")
    private TagService tagService;

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 목록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.EXPTR_REQST_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstList(
            @ModelAttribute("searchParam") ExptrReqstSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws IOException {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            searchParam = (ExptrReqstSearchParam) CmmUtils.Param.checkPrevSearchParam(baseUrl, searchParam);
            // 상단 고정 목록 조회
            model.addAttribute("exptrReqstFxdList", exptrReqstService.getFxdList());
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, "managt.managtDt", model);
            // 목록 조회
            Page<ExptrReqstDto.LIST> exptrReqstList = exptrReqstService.getPageDto(searchParam, pageRequest);
            if (exptrReqstList != null) model.addAttribute("exptrReqstList", exptrReqstList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(exptrReqstList));
            // 컨텐츠 타입에 맞는 태그 목록 조회
            model.addAttribute("tagList", tagService.getContentSpecificTagList(ContentType.EXPTR_REQST));
            // 코드 정보 모델에 추가
            cdService.setModelCdData(Constant.EXPTR_REQST_CTGR_CD, model);
            // 목록 검색 URL + 파라미터 모델에 추가
            CmmUtils.Param.setModelAttrMap(searchParam, baseUrl, model);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/reqst/exptr_reqst_list";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 등록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.EXPTR_REQST_REG_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstRegForm(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_REG));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute("post", new ExptrReqstDto());
            // 물품 구매 및 경조사비 신청 템플릿 조회
            // TmplatTxtDto exptrReqstTmplat = tmplatTxtService.getTmplatTxtByTmplatDef("EXPTR_REQST", null);
            // model.addAttribute("exptrReqstTmplat", exptrReqstTmplat);
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_REG, true);
            // 코드 정보 모델에 추가
            cdService.setModelCdData(Constant.EXPTR_REQST_CTGR_CD, model);
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/reqst/exptr_reqst_reg_form";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 등록 전 미리보기 팝업 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.EXPTR_REQST_REG_PREVIEW_POP)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstRegPreviewPop(
            final ExptrReqstDto exptrReqstDto,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_POP));
        
        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 모델에 추가
            model.addAttribute("post", exptrReqstDto);

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

        return "/view/exptr/reqst/exptr_reqst_preview_pop";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {Url.EXPTR_REQST_REG_AJAX, Url.EXPTR_REQST_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrReqstRegAjax(
            final @Valid ExptrReqstDto.DTL exptrReqst,
            final @RequestParam("postNo") @Nullable Integer key,
            final @RequestParam("jandiYn") @Nullable String jandiYn,
            final @RequestParam("trgetTopic") @Nullable String trgetTopic,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // Validation
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            // 등록/수정 처리
            boolean isReg = key == null;
            ExptrReqstDto result = isReg ? exptrReqstService.regist(exptrReqst, request) : exptrReqstService.modify(exptrReqst, request);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            if (isSuccess) {
                // 조치자 추가 :: 메인 로직과 분리
                publisher.publishEvent(new ViewerAddEvent(this, result.getClsfKey()));
                // 잔디 메세지 발송 :: 메인 로직과 분리
                // if (isSuccess && "Y".equals(jandiYn)) {
                //     String jandiRsltMsg = notifyService.notifyExptrReqstReg(trgetTopic, result, logParam);
                //     rsltMsg = rsltMsg + "\n" + jandiRsltMsg;
                // }
            }
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(exptrReqst.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 상세 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(value = Url.EXPTR_REQST_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstDtl(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            ExptrReqstDto rsDto = exptrReqstService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            
            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            exptrReqstService.hitCntUp(key);
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/reqst/exptr_reqst_dtl";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.EXPTR_REQST_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> postDtlAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            ExptrReqstDto rsDto = exptrReqstService.getDtlDto(key);
            ajaxResponse.setRsltObj(rsDto);
            
            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            exptrReqstService.hitCntUp(key);
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 수정 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(value = Url.EXPTR_REQST_MDF_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstMdfForm(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_MDF));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            ExptrReqstDto rsDto = exptrReqstService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_MDF, key);
            // 코드 정보 모델에 추가
            cdService.setModelCdData(Constant.EXPTR_REQST_CTGR_CD, model);
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/reqst/exptr_reqst_reg_form";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.EXPTR_REQST_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrReqstDelAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제 처리
            isSuccess = exptrReqstService.delete(key);
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 처리완료 (Ajax)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {Url.EXPTR_REQST_CF_AJAX})
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrReqstCfAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상태 변경 처리
            ExptrReqstDto result = exptrReqstService.exptrReqstCf(key);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 '처리안함' 처리 (Ajax)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {Url.EXPTR_REQST_DISMISS_AJAX})
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrReqstDismissAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상태 변경 처리
            ExptrReqstDto result = exptrReqstService.exptrReqstDismiss(key);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

}
