package io.nicheblog.dreamdiary.web.controller.exptr.prsnl.papr;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.PdfBoxUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprSearchParam;
import io.nicheblog.dreamdiary.web.service.cmm.tag.TagService;
import io.nicheblog.dreamdiary.web.service.exptr.prsnl.papr.ExptrPrsnlPaprService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExptrPrsnlPaprController
 * <pre>
 *  경비 관리 > 경비지출서 관리 컨트롤러
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class ExptrPrsnlPaprController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.EXPTR_PRSNL_PAPR_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_PRSNL_PAPR;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "exptrPrsnlPaprService")
    private ExptrPrsnlPaprService exptrPrsnlPaprService;
    @Resource(name = "cdService")
    private CdService cdService;
    @Resource(name = "tagService")
    private TagService tagService;

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 목록 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.EXPTR_PRSNL_PAPR_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrPrsnlList(
            @ModelAttribute("searchParam") ExptrPrsnlPaprSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            searchParam = (ExptrPrsnlPaprSearchParam) CmmUtils.Param.checkPrevSearchParam(baseUrl, searchParam);
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            Sort sort = Sort.by(Sort.Direction.DESC, "yy")
                            .and(Sort.by(Sort.Direction.DESC, "mnth"))
                            .and(Sort.by(Sort.Direction.ASC, "cfYn"))
                            .and(Sort.by(Sort.Direction.DESC, "managt.managtDt"));
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, sort, model);
            // 목록 조회
            Page<ExptrPrsnlPaprDto.LIST> exptrPrsnlList = exptrPrsnlPaprService.getPageDto(searchParam, pageRequest);
            if (exptrPrsnlList != null) model.addAttribute("exptrPrsnlList", exptrPrsnlList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(exptrPrsnlList));
            // 컨텐츠 타입에 맞는 태그 목록 조회
            model.addAttribute("tagList", tagService.getContentSpecificTagList(ContentType.EXPTR_PRSNL_PAPR));
            // 코드 정보 모델에 추가
            cdService.setModelCdData(Constant.YY_CD, model);
            cdService.setModelCdData(Constant.MNTH_CD, model);
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
            logParam.setResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_list";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 기존 작성중인 정보 존재여부 체크
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = Url.EXPTR_PRSNL_PAPR_EXISTING_CHCK_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrPrsnlExistingChckAjax(
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 존재여부 체크 및 응답에 추가
            Map<String, Object> resultMap = exptrPrsnlPaprService.exptrPrsnlExistingChck();
            ajaxResponse.setRsltMap((HashMap<String, Object>) resultMap);

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
     * 경비 관리 > 경비지출서 > 경비지출서 년도/월에 기존 작성중인 정보 있는지 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(value = Url.EXPTR_PRSNL_PAPR_YY_MNTH_CHCK_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrPrsnlYyMnthChckAjax(
            final @RequestParam("yy") String yyStr,
            final @RequestParam("mnth") String mnthStr,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상태여부 체크 및 응답에 추가
            Integer yy = Integer.parseInt(yyStr);
            Integer mnth = Integer.parseInt(mnthStr);
            BasePostDto rsltObj = exptrPrsnlPaprService.exptrPrsnlYyMnthChck(yy, mnth);
            ajaxResponse.setRsltObj(rsltObj);

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
     * 경비 관리 > 경비지출서 > 경비지출서 등록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.EXPTR_PRSNL_PAPR_REG_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrPrsnlRegForm(
            final @RequestParam("prevYn") @Nullable String prevYn,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_REG));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute("post", new ExptrPrsnlPaprDto());
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_REG, true);
            // 목록화면에서 체크 후 이전달 등록화면으로 보낼 떄 쓰는 플래그 세팅
            if (StringUtils.isNotEmpty(prevYn)) model.addAttribute("prevYn", prevYn);
            // 전년도/전월 값 세팅
            Integer[] prevYyMnth = DateUtils.getPrevYyMnth();
            Integer[] currYyMnth = DateUtils.getCurrYyMnth();
            model.addAttribute("prevYy", prevYyMnth[0]);
            model.addAttribute("currYy", currYyMnth[0]);
            model.addAttribute("prevMnth", prevYyMnth[1]);
            model.addAttribute("currMnth", currYyMnth[1]);
            // 코드 정보 모델에 추가
            cdService.setModelCdData(Constant.EXPTR_CD, model);

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

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_reg_form";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {Url.EXPTR_PRSNL_PAPR_REG_AJAX, Url.EXPTR_PRSNL_PAPR_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrPrsnlRegAjax(
            final @Valid ExptrPrsnlPaprDto.DTL exptrPrsnlPapr,
            final @RequestParam("postNo") @Nullable Integer key,
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
            ExptrPrsnlPaprDto result = isReg ? exptrPrsnlPaprService.regist(exptrPrsnlPapr, request) : exptrPrsnlPaprService.modify(exptrPrsnlPapr, request);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            if (isSuccess) {
                // 조치자 추가 :: 메인 로직과 분리
                publisher.publishEvent(new ViewerAddEvent(this, result.getClsfKey()));
            }
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(exptrPrsnlPapr.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 상세 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.EXPTR_PRSNL_PAPR_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrPrsnlDtl(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            exptrPrsnlPaprService.hitCntUp(key);
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

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_dtl";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 출력 팝업 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.EXPTR_PRSNL_PAPR_PDF_POP)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrPrsnlPdfPop(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_POP));
        model.addAttribute("isPdf", true);      // 관련 세팅 위해 필요 TODO: 레이아웃 자체에 두기...

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);

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

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_dtl_pdf_pop";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 수정 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.EXPTR_PRSNL_PAPR_MDF_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrPrsnlMdfForm(
            final @RequestParam("postNo") Integer key,
            final @RequestParam("mngrYn") @Nullable String mngrYn,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_MDF));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_MDF, true);
            // 관리자 경비관리 화면에서 넘어왔는지 여부 세팅
            model.addAttribute("mngrYn", mngrYn);            
            // 전년도/전월 값 세팅
            Integer[] prevYyMnth = DateUtils.getPrevYyMnth();
            Integer[] currYyMnth = DateUtils.getCurrYyMnth();
            model.addAttribute("prevYy", prevYyMnth[0]);
            model.addAttribute("currYy", currYyMnth[0]);
            model.addAttribute("prevMnth", prevYyMnth[1] + 1);
            model.addAttribute("currMnth", currYyMnth[1] + 1);
            // 코드 정보 모델에 추가
            cdService.setModelCdData(Constant.EXPTR_CD, model);

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

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_reg_form";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.EXPTR_PRSNL_PAPR_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrPrsnlDelAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제 처리
            isSuccess = exptrPrsnlPaprService.delete(key);
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
     * 경비 관리 > 경비지출서 > 영수증 이미지파일 묶음 PDF 다운로드
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.EXPTR_PRSNL_PAPR_RCIPT_PDF_DOWNLOAD)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public void exptrPrsnlStatsRciptPdfDownload(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) throws Exception {

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 첨부파일 목록 조회
            ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            List<AtchFileDtlDto> fileList = exptrPrsnlPaprService.getExptrPrsnlRciptList(key);
            // PDF 파일 생성 및 다운로드
            String fileNm = rsDto.getRegstrNm() + "_" + rsDto.getTitle() + "_" + DateUtils.getCurrDateStr(DatePtn.PDATETIME) + ".pdf";
            PdfBoxUtils.imgCmbnPdfDonwload(fileNm, fileList);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.EXPTR_PRSNL_STATS_PAGE);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }
}