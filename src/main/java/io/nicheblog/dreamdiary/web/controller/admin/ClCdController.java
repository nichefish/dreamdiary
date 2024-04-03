package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCd;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.admin.ClCdSearchParam;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.service.admin.ClCdService;
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

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;

/**
 * ClCdController
 * <pre>
 *  분류코드 정보 관리 컨트롤러
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class ClCdController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.CL_CD_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.CD;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "clCdService")
    private ClCdService clCdService;

    /**
     * 분류 코드(CL_CD) 관리 (useYn=N 포함) 목록 화면 조회
     * (관리자MNGR만 접근 가능)
     */
    @GetMapping(SiteUrl.CL_CD_LIST)
    @Secured({Constant.ROLE_MNGR})
    public String clCdList(
            @ModelAttribute("searchParam") ClCdSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.CD.setAcsPageInfo("로그인 정책 관리"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            searchParam = (ClCdSearchParam) CmmUtils.Param.checkPrevSearchParam(baseUrl, searchParam);
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, "clCd", model);
            // 목록 조회
            Page<ClCd> clCdList = clCdService.getListDto(searchParam, pageRequest);
            model.addAttribute("clCdList", clCdList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(clCdList));
            // 목록 검색 URL + 파라미터 모델에 추가
            CmmUtils.Param.setModelAttrMap(searchParam, baseUrl, model);

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
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

        return "/view/admin/cd/cl_cd_list";
    }

    /**
     * 분류 코드(CL_CD) 관리 (useYn=N 포함) 상세 화면 조회
     * (관리자MNGR만 접근 가능)
     */
    @GetMapping(SiteUrl.CL_CD_DTL)
    @Secured({Constant.ROLE_MNGR})
    public String clCdDtl(
            final LogActvtyParam logParam,
            final @RequestParam("clCd") String clCd,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.CD.setAcsPageInfo("로그인 정책 관리"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ClCd cmmClCd = clCdService.getDtlDto(clCd);
            model.addAttribute("clCd", cmmClCd);
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

        return "/view/admin/cd/cl_cd_dtl";
    }

    /**
     * 분류 코드(CL_CD) 관리 (useYn=N 포함) 등록/수정 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {SiteUrl.CL_CD_REG_AJAX, SiteUrl.CL_CD_MDF_AJAX})
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> clCdRegAjax(
            final @Valid ClCd clCd,
            final String key,
            final LogActvtyParam logParam,
            final @RequestParam("regYn") String regYn,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            bindingResult.hasErrors();
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = "Y".equals(regYn);
            ClCd rsDto = isReg ? clCdService.regist(clCd) : clCdService.modify(clCd, key);
            isSuccess = (rsDto.getClCd() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(clCd.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 분류 코드(CL_CD) 관리 (useYn=N 포함) 삭제 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.CL_CD_DEL_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> clCdDelAjax(
            final LogActvtyParam logParam,
            final @RequestParam("clCd") String clCd
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = clCdService.delete(clCd);
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
     * 분류 코드 관리(useYn=N 포함) 상세 데이터 조회 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.CL_CD_DTL_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> clCdDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("clCd") String clCd
    ) throws Exception {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            ClCd cmmClCd = clCdService.getDtlDto(clCd);
            ajaxResponse.setResultObj(cmmClCd);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 분류 코드 관리(useYn=N 포함) 사용 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.CL_CD_USE_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> clCdUseAjax(
            final LogActvtyParam logParam,
            final @RequestParam("clCd") String clCd
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = clCdService.setStateUse(clCd);
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
