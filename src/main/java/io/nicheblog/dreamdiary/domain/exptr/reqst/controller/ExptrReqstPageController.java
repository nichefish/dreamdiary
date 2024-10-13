package io.nicheblog.dreamdiary.domain.exptr.reqst.controller;

import io.nicheblog.dreamdiary.domain.exptr.reqst.model.ExptrReqstDto;
import io.nicheblog.dreamdiary.domain.exptr.reqst.model.ExptrReqstSearchParam;
import io.nicheblog.dreamdiary.domain.exptr.reqst.service.ExptrReqstService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.domain.admin.menu.SiteMenu;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common._clsf.tag.service.TagService;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.global._common.cd.service.DtlCdService;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.PaginationInfo;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * ExptrReqstController
 * <pre>
 *  경비 관리 > 물품구매/경조사비 신청 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class ExptrReqstPageController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.EXPTR_REQST_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_REQST;        // 작업 카테고리 (로그 적재용)

    private final DtlCdService dtlCdService;
    private final ExptrReqstService exptrReqstService;
    private final TagService tagService;

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 목록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
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
            final PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, "managt.managtDt", model);
            // 목록 조회
            final Page<ExptrReqstDto.LIST> exptrReqstList = exptrReqstService.getPageDto(searchParam, pageRequest);
            if (exptrReqstList != null) model.addAttribute("exptrReqstList", exptrReqstList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(exptrReqstList));
            // 컨텐츠 타입에 맞는 태그 목록 조회
            model.addAttribute("tagList", tagService.getContentSpecificTagList(ContentType.EXPTR_REQST));
            // 코드 정보 모델에 추가
            dtlCdService.setCdListToModel(Constant.EXPTR_REQST_CTGR_CD, model);
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

        return "/view/domain/exptr/reqst/exptr_reqst_list";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 등록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.EXPTR_REQST_REG_FORM)
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
            dtlCdService.setCdListToModel(Constant.EXPTR_REQST_CTGR_CD, model);
            dtlCdService.setCdListToModel(Constant.MDFABLE_CD, model);
            dtlCdService.setCdListToModel(Constant.JANDI_TOPIC_CD, model);

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

        return "/view/domain/exptr/reqst/exptr_reqst_reg_form";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 등록 전 미리보기 팝업 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param exptrReqst 작성 중인 게시물
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     */
    @PostMapping(Url.EXPTR_REQST_REG_PREVIEW_POP)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String exptrReqstRegPreviewPop(
            final ExptrReqstDto exptrReqst,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        model.addAttribute(Constant.SITE_MENU, SiteMenu.EXPTR_REQST.setAcsPageInfo(Constant.PAGE_POP));
        
        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 모델에 추가
            exptrReqst.setMarkdownCn(CmmUtils.markdown(exptrReqst.getCn()));
            model.addAttribute("post", exptrReqst);

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

        return "/view/domain/exptr/reqst/exptr_reqst_preview_pop";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 상세 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.EXPTR_REQST_DTL)
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
            final ExptrReqstDto rsDto = exptrReqstService.getDtlDto(key);
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

        return "/view/domain/exptr/reqst/exptr_reqst_dtl";
    }

    /**
     * 경비 관리 > 물품 구매 및 경조사비 신청 수정 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.EXPTR_REQST_MDF_FORM)
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
            final ExptrReqstDto rsDto = exptrReqstService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_MDF, key);
            // 코드 정보 모델에 추가
            dtlCdService.setCdListToModel(Constant.EXPTR_REQST_CTGR_CD, model);
            dtlCdService.setCdListToModel(Constant.MDFABLE_CD, model);
            dtlCdService.setCdListToModel(Constant.JANDI_TOPIC_CD, model);

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

        return "/view/domain/exptr/reqst/exptr_reqst_reg_form";
    }
}
