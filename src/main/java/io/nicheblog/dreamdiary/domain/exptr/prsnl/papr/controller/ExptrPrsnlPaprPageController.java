package io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.controller;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model.ExptrPrsnlPaprSearchParam;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.service.ExptrPrsnlPaprService;
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
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nullable;

/**
 * ExptrPrsnlPaprController
 * <pre>
 *  경비 관리 > 경비지출서 관리 컨트롤러.
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class ExptrPrsnlPaprPageController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.EXPTR_PRSNL_PAPR_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_PRSNL_PAPR;        // 작업 카테고리 (로그 적재용)

    private final ExptrPrsnlPaprService exptrPrsnlPaprService;
    private final DtlCdService dtlCdService;
    private final TagService tagService;

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 목록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
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
            final Sort sort = Sort.by(Sort.Direction.DESC, "yy")
                            .and(Sort.by(Sort.Direction.DESC, "mnth"))
                            .and(Sort.by(Sort.Direction.ASC, "cfYn"))
                            .and(Sort.by(Sort.Direction.DESC, "managt.managtDt"));
            final PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, sort, model);
            // 목록 조회
            final Page<ExptrPrsnlPaprDto.LIST> exptrPrsnlList = exptrPrsnlPaprService.getPageDto(searchParam, pageRequest);
            if (exptrPrsnlList != null) model.addAttribute("exptrPrsnlList", exptrPrsnlList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(exptrPrsnlList));
            // 컨텐츠 타입에 맞는 태그 목록 조회
            model.addAttribute("tagList", tagService.getContentSpecificTagList(ContentType.EXPTR_PRSNL_PAPR));
            // 코드 정보 모델에 추가
            dtlCdService.setCdListToModel(Constant.YY_CD, model);
            dtlCdService.setCdListToModel(Constant.MNTH_CD, model);
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

        return "/view/domain/exptr/prsnl/papr/exptr_prsnl_papr_list";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 등록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     * 
     * @param prevYn 이전 월 등록화면 여부 (Y/N)
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.EXPTR_PRSNL_PAPR_REG_FORM)
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
            final Integer[] prevYyMnth = DateUtils.getPrevYyMnth();
            final Integer[] currYyMnth = DateUtils.getCurrYyMnth();
            model.addAttribute("prevYy", prevYyMnth[0]);
            model.addAttribute("currYy", currYyMnth[0]);
            model.addAttribute("prevMnth", prevYyMnth[1]);
            model.addAttribute("currMnth", currYyMnth[1]);
            // 코드 정보 모델에 추가
            dtlCdService.setCdListToModel(Constant.EXPTR_CD, model);

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

        return "/view/domain/exptr/prsnl/papr/exptr_prsnl_papr_reg_form";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 상세 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.EXPTR_PRSNL_PAPR_DTL)
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
            final ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
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

        return "/view/domain/exptr/prsnl/papr/exptr_prsnl_papr_dtl";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 출력용 팝업 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     */
    @GetMapping(Url.EXPTR_PRSNL_PAPR_PDF_POP)
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
            final ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
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

        return "/view/domain/exptr/prsnl/papr/exptr_prsnl_papr_dtl_pdf_pop";
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 수정 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param mngrYn 관리자 여부 (Y/N)
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.EXPTR_PRSNL_PAPR_MDF_FORM)
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
            final ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_MDF, true);
            // 관리자 경비관리 화면에서 넘어왔는지 여부 세팅
            model.addAttribute("mngrYn", mngrYn);            
            // 전년도/전월 값 세팅
            final Integer[] prevYyMnth = DateUtils.getPrevYyMnth();
            final Integer[] currYyMnth = DateUtils.getCurrYyMnth();
            model.addAttribute("prevYy", prevYyMnth[0]);
            model.addAttribute("currYy", currYyMnth[0]);
            model.addAttribute("prevMnth", prevYyMnth[1] + 1);
            model.addAttribute("currMnth", currYyMnth[1] + 1);
            // 코드 정보 모델에 추가
            dtlCdService.setCdListToModel(Constant.EXPTR_CD, model);

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

        return "/view/domain/exptr/prsnl/papr/exptr_prsnl_papr_reg_form";
    }
}