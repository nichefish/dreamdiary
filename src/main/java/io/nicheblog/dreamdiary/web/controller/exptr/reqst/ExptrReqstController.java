package io.nicheblog.dreamdiary.web.controller.exptr.reqst;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.CmmUtils;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstDto;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstListDto;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstSearchParam;
import io.nicheblog.dreamdiary.web.service.admin.TmplatTxtService;
import io.nicheblog.dreamdiary.web.service.cmm.NotifyService;
import io.nicheblog.dreamdiary.web.service.exptr.reqst.ExptrReqstService;
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
import java.util.List;
import java.util.Map;

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

    // 작업 카테고리 (로그 적재용)
    private final String baseUrl = SiteUrl.EXPTR_REQST_LIST;
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_REQST;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "cdService")
    public CdService cdService;

    @Resource(name = "exptrReqstService")
    public ExptrReqstService exptrReqstService;

    // @Resource(name = "boardPostViewerService")
    // private BoardPostViewerService boardPostViewerService;

    @Resource(name = "notifyService")
    private NotifyService notifyService;

    @Resource(name = "tmplatTxtService")
    private TmplatTxtService tmplatTxtService;

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 목록 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @GetMapping(SiteUrl.EXPTR_REQST_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstList(
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") ExptrReqstSearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap,
            final ModelMap model
    ) throws IOException {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            Map<String, Object> listParamMap = CmmUtils.checkPrevSearchMap(searchParamMap, baseUrl, searchParam);

            // 상단 고정 목록 조회
            List<ExptrReqstListDto> exptrReqstFxdList = exptrReqstService.getFxdList();
            model.addAttribute("exptrReqstFxdList", exptrReqstFxdList);
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, "regDt", model);
            // PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, "managt.managtDt", model);
            Page<ExptrReqstListDto> exptrReqstList = exptrReqstService.getListDto(listParamMap, pageRequest);
            if (exptrReqstList != null) model.addAttribute("exptrReqstList", exptrReqstList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(exptrReqstList));
            cdService.setModelCdData(Constant.EXPTR_REQST_CTGR_CD, model);
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

        return "/view/exptr/reqst/exptr_reqst_list";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 등록 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.EXPTR_REQST_REG_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstRegForm(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_REG));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = true;
            model.addAttribute("post", new ExptrReqstDto());      // 빈 객체 주입 (freemarker error prevention)

            // 물품 구매 및 경조사비 신청 템플릿 조회
            // TmplatTxtDto exptrReqstTmplat = tmplatTxtService.getTmplatTxtByTmplatDef("EXPTR_REQST", null);
            // model.addAttribute("exptrReqstTmplat", exptrReqstTmplat);

            model.addAttribute(Constant.IS_REG, true);           // 등록/수정 화면 플래그 세팅
            cdService.setModelCdData(Constant.EXPTR_REQST_CTGR_CD, model);
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
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

        return "/view/exptr/reqst/exptr_reqst_reg_form";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 등록 전 미리보기 팝업 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.EXPTR_REQST_REG_PREVIEW_POP)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstRegPreviewPop(
            final ExptrReqstDto exptrReqstDto,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_POP));
        
        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = true;
            // TODO: 파일 정보는? 난해하다...
            model.addAttribute("post", exptrReqstDto);
            model.addAttribute("currDateStr", DateUtils.getCurrDateStr(DateUtils.PTN_DATETIME));
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/reqst/exptr_reqst_reg_preview_pop";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 등록/수정 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {SiteUrl.EXPTR_REQST_REG_AJAX, SiteUrl.EXPTR_REQST_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrReqstRegAjax(
            final @Valid ExptrReqstDto exptrReqstDto,
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
            boolean isReg = exptrReqstDto.getPostNo() == null;
            ExptrReqstDto result = isReg ? exptrReqstService.regist(exptrReqstDto, request) : exptrReqstService.modify(exptrReqstDto, key, request);
            isSuccess = (result.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            // 잔디 메세지 발송 :: 메인 로직과 분리
            // if (isSuccess && "Y".equals(jandiYn)) {
            //     String jandiResultMsg = notifyService.notifyExptrReqstReg(trgetTopic, result, logParam);
            //     resultMsg = resultMsg + "\n" + jandiResultMsg;
            // }
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(exptrReqstDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 상세 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(value = SiteUrl.EXPTR_REQST_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstDtl(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ExptrReqstDto rsDto = exptrReqstService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 열람자 목록 및 조회수 카운트 추가
            // try {
            //     List<BoardPostViewerDto> viewerList = rsDto.getViewerList();
            //     if (!boardPostViewerService.hasAlreadyView(viewerList)) {
            //         BoardPostViewerDto dto = boardPostViewerService.regPostViewer(postKey);
            //         rsDto.addPostViewer(dto);
            //     }
            //     exptrReqstService.hitCntUp(postKey);
            // } catch (Exception e) {
            //     resultMsg = MessageUtils.getExceptionMsg(e);
            //     logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            //     publisher.publishEvent(new LogActvtyEvent(this, logParam));
            // }
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/reqst/exptr_reqst_dtl";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 상세 조회 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.EXPTR_REQST_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> postDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 게시판 정보 조회
            ExptrReqstDto rsDto = exptrReqstService.getDtlDto(key);
            // 열람자 목록 및 조회수 카운트 추가
            // try {
            //     List<BoardPostViewerDto> viewerList = rsDto.getViewerList();
            //     if (!boardPostViewerService.hasAlreadyView(viewerList)) {
            //         BoardPostViewerDto dto = boardPostViewerService.regPostViewer(postKey);
            //         rsDto.addPostViewer(dto);
            //     }
            //     exptrReqstService.hitCntUp(postKey);
            // } catch (Exception e) {
            //     resultMsg = MessageUtils.getExceptionMsg(e);
            //     logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            //     publisher.publishEvent(new LogActvtyEvent(this, logParam));
            // }
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
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 수정 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(value = SiteUrl.EXPTR_REQST_MDF_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstMdfForm(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_MDF));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ExptrReqstDto rsDto = exptrReqstService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);

            model.addAttribute(Constant.IS_MDF, key);           // 등록/수정 화면 플래그 세팅
            cdService.setModelCdData(Constant.EXPTR_REQST_CTGR_CD, model);
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/reqst/exptr_reqst_reg_form";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 삭제 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(SiteUrl.EXPTR_REQST_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrReqstDelAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = exptrReqstService.delete(key);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 처리완료 (Ajax)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {SiteUrl.EXPTR_REQST_CF_AJAX})
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrReqstCfAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ExptrReqstDto result = exptrReqstService.exptrReqstCf(key);
            isSuccess = (result.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 '처리안함' 처리 (Ajax)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {SiteUrl.EXPTR_REQST_DISMISS_AJAX})
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrReqstDismissAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ExptrReqstDto result = exptrReqstService.exptrReqstDismiss(key);
            isSuccess = (result.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

}
