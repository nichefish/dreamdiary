package io.nicheblog.dreamdiary.web.controller.exptr.prsnl;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.util.CmmUtils;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprListDto;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprSearchParam;
import io.nicheblog.dreamdiary.web.service.exptr.prsnl.ExptrPrsnlPaprService;
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

    // 작업 카테고리 (로그 적재용)
    private final String baseUrl = SiteUrl.EXPTR_PRSNL_PAPR_LIST;
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_PRSNL_PAPR;      // 작업 카테고리 (로그 적재용)

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return actvtyCtgr.name();
    }

    @Resource(name = "cdService")
    private CdService cdService;

    @Resource(name = "exptrPrsnlService")
    private ExptrPrsnlPaprService exptrPrsnlPaprService;

    // @Resource(name = "boardPostViewerService")
    // private BoardPostViewerService boardPostViewerService;

    // @Resource(name = "pdfBoxUtils")
    // private PdfBoxUtils pdfBoxUtils;

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 목록 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @GetMapping(SiteUrl.EXPTR_PRSNL_PAPR_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrIndvdList(
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") ExptrPrsnlPaprSearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            Map<String, Object> listParamMap = CmmUtils.checkPrevSearchMap(searchParamMap, baseUrl, searchParam);

            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            Sort sort = Sort.by(Sort.Direction.DESC, "yy")
                            .and(Sort.by(Sort.Direction.DESC, "mnth"))
                            .and(Sort.by(Sort.Direction.ASC, "cfYn"));
                            //.and(Sort.by(Sort.Direction.DESC, "managtDt"))
            PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, sort, model);
            Page<ExptrPrsnlPaprListDto> exptrIndvdList = exptrPrsnlPaprService.getListDto(listParamMap, pageRequest);
            if (exptrIndvdList != null) model.addAttribute("exptrIndvdList", exptrIndvdList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(exptrIndvdList));
            cdService.setModelCdData(Constant.YY_CD, model);
            cdService.setModelCdData(Constant.MNTH_CD, model);
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
            logParam.setResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_list";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 기존 작성중인 정보 존재여부 체크
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(value = SiteUrl.EXPTR_PRSNL_PAPR_EXISTING_CHCK_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrIndvdExistingChckAjax(
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Map<String, Object> resultMap = exptrPrsnlPaprService.exptrIndvdExistingChck();
            ajaxResponse.setResultMap((HashMap<String, Object>) resultMap);
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
     * 경비 관리 > 경비지출서 > 경비지출서 등록 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.EXPTR_PRSNL_PAPR_REG_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrIndvdRegForm(
            final LogActvtyParam logParam,
            final @RequestParam("prevYn") @Nullable String prevYn,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_REG));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = true;
            model.addAttribute("post", new ExptrPrsnlPaprDto());      // 빈 객체 주입 (freemarker error prevention)
            Integer[] prevYyMnth = DateUtils.getPrevYyMnth();
            Integer[] currYyMnth = DateUtils.getCurrYyMnth();
            model.addAttribute("prevYy", prevYyMnth[0]);
            model.addAttribute("currYy", currYyMnth[0]);
            model.addAttribute("prevMnth", prevYyMnth[1]);
            model.addAttribute("currMnth", currYyMnth[1]);
            if (StringUtils.isNotEmpty(prevYn)) model.addAttribute("prevYn", prevYn);           // 등록화면에서 체크 후 이전달 등록화면으로 보낼 떄 플래그
            model.addAttribute(Constant.IS_REG, true);           // 등록/수정 화면 플래그 세팅
            cdService.setModelCdData(Constant.EXPTR_TY_CD, model);
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

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_reg_form";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 년도/월에 기존 작성중인 정보 있는지 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(value = SiteUrl.EXPTR_PRSNL_PAPR_YY_MNTH_CHCK_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrIndvdYyMnthChckAjax(
            final LogActvtyParam logParam,
            final @RequestParam("yy") String yyStr,
            final @RequestParam("mnth") String mnthStr
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer yy = Integer.parseInt(yyStr);
            Integer mnth = Integer.parseInt(mnthStr);
            BasePostDto resultObj = exptrPrsnlPaprService.exptrIndvdYyMnthChck(yy, mnth);
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
     * 경비 관리 > 경비지출서 > 경비지출서 등록/수정 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {SiteUrl.EXPTR_PRSNL_PAPR_REG_AJAX, SiteUrl.EXPTR_PRSNL_PAPR_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrIndvdRegAjax(
            final @Valid ExptrPrsnlPaprDto exptrPrsnlPaprDto,
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = key == null;
            ExptrPrsnlPaprDto result = isReg ? exptrPrsnlPaprService.regist(exptrPrsnlPaprDto, request) : exptrPrsnlPaprService.modify(exptrPrsnlPaprDto, key, request);
            isSuccess = (result.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(exptrPrsnlPaprDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 상세 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.EXPTR_PRSNL_PAPR_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrIndvdDtl(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 열람자 목록 및 조회수 카운트 추가
            // try {
            //     List<BoardPostViewerDto> viewerList = rsDto.getViewerList();
            //     if (!boardPostViewerService.hasAlreadyView(viewerList)) {
            //         BoardPostViewerDto dto = boardPostViewerService.regPostViewer(postKey);
            //         rsDto.addPostViewer(dto);
            //     }
            //     exptrPrsnlService.hitCntUp(postKey);
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

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_dtl";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 출력 팝업 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.EXPTR_PRSNL_PAPR_PDF_POP)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrIndvdPdfPop(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_POP));
        model.addAttribute("isPdf", true);      // 관련 세팅 위해 필요 TODO: 레이아웃 자체에 두기...

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);
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

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_dtl_pdf_pop";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 수정 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.EXPTR_PRSNL_PAPR_MDF_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrIndvdMdfForm(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final @RequestParam("mngrYn") @Nullable String mngrYn,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_PRSNL_PAPR.setAcsPageInfo(Constant.PAGE_MDF));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            isSuccess = true;

            model.addAttribute(Constant.IS_MDF, true);           // 등록/수정 화면 플래그 세팅
            model.addAttribute("mngrYn", mngrYn);                   // 관리자 화면에서 넘어왔는지 여부 세팅
            Integer[] prevYyMnth = DateUtils.getPrevYyMnth();
            Integer[] currYyMnth = DateUtils.getCurrYyMnth();
            model.addAttribute("prevYy", prevYyMnth[0]);
            model.addAttribute("currYy", currYyMnth[0]);
            model.addAttribute("prevMnth", prevYyMnth[1] + 1);
            model.addAttribute("currMnth", currYyMnth[1] + 1);
            cdService.setModelCdData(Constant.EXPTR_TY_CD, model);
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

        return "/view/exptr/prsnl/papr/exptr_prsnl_papr_reg_form";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 삭제 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(SiteUrl.EXPTR_PRSNL_PAPR_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrIndvdDelAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = exptrPrsnlPaprService.delete(key);
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
     * 경비 관리 > 경비지출서 > 영수증 이미지파일 묶음 PDF 다운로드
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    // @RequestMapping(SiteUrl.EXPTR_PRSNL_PAPR_RCIPT_PDF_DOWNLOAD)
    // @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    // public void exptrIndvdStatsRciptPdfDownload(
    //         final LogActvtyParam logParam,
    //         final @RequestParam("postNo") Integer key
    // ) throws Exception {
//
    //     boolean isSuccess = false;
    //     String resultMsg = "";
    //     try {
    //         ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
    //         String fileNm = rsDto.getRegstrNm() + "_" + rsDto.getTitle() + "_" + DateUtils.getCurrDateStr(DateUtils.PTN_PDATETIME) + ".pdf";
    //         List<AtchFileDtlDto> fileList = exptrPrsnlPaprService.getExptrPrsnlRciptList(key);
    //         pdfBoxUtils.cmbnPdfDonwload(fileNm, fileList);
    //         isSuccess = true;
    //         resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
    //     } catch (Exception e) {
    //         isSuccess = false;
    //         resultMsg = MessageUtils.getExceptionMsg(e);
    //         logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
    //         MessageUtils.alertMessage(resultMsg, SiteUrl.EXPTR_PRSNL_STATS);
    //     } finally {
    //         // 로그 관련 처리
    //         logParam.setCn("key: " + key);
    //         logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
    //         publisher.publishEvent(new LogActvtyEvent(this, logParam));
    //     }
    // }
}