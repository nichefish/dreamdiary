package io.nicheblog.dreamdiary.web.controller.jrnl.sbjct;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.event.ManagtrAddEvent;
import io.nicheblog.dreamdiary.web.event.TagProcEvent;
import io.nicheblog.dreamdiary.web.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.jrnl.sbjct.JrnlSbjctDto;
import io.nicheblog.dreamdiary.web.model.jrnl.sbjct.JrnlSbjctSearchParam;
import io.nicheblog.dreamdiary.web.service.cmm.tag.TagService;
import io.nicheblog.dreamdiary.web.service.jrnl.sbjct.JrnlSbjctService;
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
import java.security.InvalidParameterException;

/**
 * JrnlSbjctController
 * <pre>
 *  저널 주제 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class JrnlSbjctController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.JRNL_SBJCT_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.NOTICE;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "jrnlSbjctService")
    private JrnlSbjctService jrnlSbjctService;
    @Resource(name = "cdService")
    private CdService cdService;
    @Resource(name = "tagService")
    private TagService tagService;

    /**
     * 저널 주제 목록 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.JRNL_SBJCT_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String jrnlSbjctList(
            @ModelAttribute("searchParam") JrnlSbjctSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.JRNL_SBJCT.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 :: 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            searchParam = (JrnlSbjctSearchParam) CmmUtils.Param.checkPrevSearchParam(baseUrl, searchParam);
            // 상단 고정 목록 조회
            model.addAttribute("jrnlSbjctFxdList", jrnlSbjctService.getFxdList());
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, "regDt", model);
            // 목록 조회 및 모델에 추가
            Page<JrnlSbjctDto.LIST> jrnlSbjctList = jrnlSbjctService.getPageDto(searchParam, pageRequest);
            model.addAttribute("jrnlSbjctList", jrnlSbjctList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(jrnlSbjctList));
            // 컨텐츠 타입에 맞는 태그 목록 조회
            model.addAttribute("tagList", tagService.getContentSpecificTagList(ContentType.JRNL_SBJCT));
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

        return "/view/jrnl/sbjct/jrnl_sbjct_list";
    }

    /**
     * 저널 주제 등록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.JRNL_SBJCT_REG_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String jrnlSbjctRegForm(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.JRNL_SBJCT.setAcsPageInfo(Constant.PAGE_REG));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute("post", new JrnlSbjctDto());
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_REG, true);
            // 코드 정보 모델에 추가
            // cdService.setModelCdData(Constant.JRNL_SBJCT_CTGR_CD, model);
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
            // cmmService.setModelFlsysPath(model);
            
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

        return "/view/jrnl/sbjct/jrnl_sbjct_reg_form";
    }

    /**
     * 저널 주제 등록 전 미리보기 팝업 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.JRNL_SBJCT_REG_PREVIEW_POP)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String jrnlSbjctRegPreviewPop(
            final @Valid JrnlSbjctDto jrnlSbjct,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        model.addAttribute(Constant.SITE_MENU, SiteMenu.JRNL_SBJCT.setAcsPageInfo("저널 주제 미리보기"));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 정보 모델에 추가
            jrnlSbjct.setMarkdownCn(CmmUtils.markdown(jrnlSbjct.getCn()));
            model.addAttribute("post", jrnlSbjct);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            // 로그 관련 처리
            logParam.setCn(jrnlSbjct.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/jrnl/sbjct/jrnl_sbjct_preview_pop";
    }

    /**
     * 저널 주제 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {Url.JRNL_SBJCT_REG_AJAX, Url.JRNL_SBJCT_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSbjctRegAjax(
            final @Valid JrnlSbjctDto.DTL jrnlSbjct,
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
            boolean isReg = (key == null);
            JrnlSbjctDto result = isReg ? jrnlSbjctService.regist(jrnlSbjct, request) : jrnlSbjctService.modify(jrnlSbjct, request);
            ajaxResponse.setRsltObj(result);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            if (isSuccess) {
                // 태그 처리 :: 메인 로직과 분리
                publisher.publishEvent(new TagProcEvent(this, result.getClsfKey(), jrnlSbjct.tag));
                // 조치자 추가 :: 메인 로직과 분리
                publisher.publishEvent(new ManagtrAddEvent(this, result.getClsfKey()));
                // 잔디 메세지 발송 :: 메인 로직과 분리
                // if ("Y".equals(jandiYn)) {
                //     String jandiRsltMsg = notifyService.notifyJrnlSbjctReg(trgetTopic, result, logParam);
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
            logParam.setCn(jrnlSbjct.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 저널 주제 상세 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.JRNL_SBJCT_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String jrnlSbjctDtl(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.JRNL_SBJCT.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            JrnlSbjctDto rsDto = jrnlSbjctService.getDtlDto(key);
            model.addAttribute("post", rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            jrnlSbjctService.hitCntUp(key);
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/jrnl/sbjct/jrnl_sbjct_dtl";
    }

    /**
     * 저널 주제 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.JRNL_SBJCT_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSbjctDtlAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            JrnlSbjctDto rsDto = jrnlSbjctService.getDtlDto(key);
            ajaxResponse.setRsltObj(rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            jrnlSbjctService.hitCntUp(key);
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 저널 주제 수정 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.JRNL_SBJCT_MDF_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String jrnlSbjctMdfForm(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.JRNL_SBJCT.setAcsPageInfo(Constant.PAGE_MDF));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            JrnlSbjctDto rsDto = jrnlSbjctService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_MDF, true);
            // 코드 정보 모델에 추가
            // cdService.setModelCdData(Constant.JRNL_SBJCT_CTGR_CD, model);
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
            // cmmService.setModelFlsysPath(model);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/jrnl/sbjct/jrnl_sbjct_reg_form";
    }

    /**
     * 저널 주제 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.JRNL_SBJCT_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSbjctDelAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제 처리
            isSuccess = jrnlSbjctService.delete(key);
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            if (isSuccess) {
                // 태그 처리 :: 메인 로직과 분리
                publisher.publishEvent(new TagProcEvent(this, new BaseClsfKey(key, ContentType.NOTICE)));
            }
        } catch (Exception e) {
            logParam.setExceptionInfo(e);

            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 휴가 관리 > 휴가사용일자 > 휴가사용일자 엑셀 다운로드
     * 관리자MNGR만 접근 가능
     */
    // @GetMapping(Url.JRNL_SBJCT_LIST_XLSX_DOWNLOAD)
    // @Secured(Constant.ROLE_MNGR)
    // public void vcatnSchdulXlsxDownload(
    //         final JrnlSbjctSearchParam searchParam,
    //         final LogActvtyParam logParam
    // ) throws Exception {
//
    //     boolean isSuccess = false;
    //     String rsltMsg = "";
    //     try {
    //         // 접근방식 1. stream 객체 전달
    //         Stream<JrnlSbjctXlsxDto> xlsxStream = jrnlSbjctService.getStreamXlsxDto(searchParam);
    //         XlsxUtils.listXlxsDownload(XlsxType.NOTICE, xlsxStream);
    //         // 접근방식 2. ???
//
    //         isSuccess = true;
    //         rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
    //     } catch (Exception e) {
    //         isSuccess = false;
    //         rsltMsg = MessageUtils.getExceptionMsg(e);
    //         logParam.setExceptionInfo(e);
    //         MessageUtils.alertMessage(rsltMsg, Url.VCATN_SCHDUL_LIST);
    //     } finally {
    //         // 로그 관련 처리
    //         logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
    //         publisher.publishEvent(new LogActvtyEvent(this, logParam));
    //     }
    // }

}