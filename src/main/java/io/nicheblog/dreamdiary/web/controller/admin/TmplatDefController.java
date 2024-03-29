package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.CmmUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.admin.TmplatDefDto;
import io.nicheblog.dreamdiary.web.model.admin.TmplatDefSearchParam;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.cmm.SiteAcsInfo;
import io.nicheblog.dreamdiary.web.service.admin.TmplatDefService;
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
 * TmplatDefController
 * <pre>
 *  템플릿 정의 관리 컨트롤러
 * </pre>
 * TODO: 신규개발 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class TmplatDefController
        extends BaseControllerImpl {

    // 작업 카테고리 (로그 적재용)
    private final String baseUrl = SiteUrl.TMPLAT_DEF_LIST;
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.TMPLAT;        // 작업 카테고리 (로그 적재용)

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return actvtyCtgr.name();
    }

    @Resource(name = "tmplatDefService")
    private TmplatDefService tmplatDefService;

    /**
     * 템플릿 정의 목록 조회
     * 관리자MNGR만 접근 가능
     */
    @GetMapping(SiteUrl.TMPLAT_DEF_LIST)
    @Secured({Constant.ROLE_MNGR})
    public String tmplatDefList(
            final @ModelAttribute(Constant.SITE_MENU) SiteAcsInfo siteMenuAcsInfo,
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") TmplatDefSearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap,
            final ModelMap model
    ) throws Exception {

        model.addAttribute(Constant.SITE_MENU, SiteMenu.TMPLAT.setAcsPageInfo("템플릿 관리"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            Map<String, Object> listParamMap = CmmUtils.checkPrevSearchMap(searchParamMap, baseUrl, searchParam);

            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, "regDt", model);
            Page<TmplatDefDto> tmplatList = tmplatDefService.getListDto(listParamMap, pageRequest);
            if (tmplatList != null) model.addAttribute("tmplatList", tmplatList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(tmplatList));
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);

            // 검색 파라미터 다시 모델에 추가
            CmmUtils.setModelAttrMap(listParamMap, searchParam, baseUrl, model);
            // 관리자페이지 화면 모드 세팅
            session.setAttribute("userMode", Constant.AUTH_MNGR);
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

        return "/view/tmplat/def/tmplat_def_list";
    }

    /**
     * 템플릿 정의 등록/수정 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     */
    @PostMapping(value = {SiteUrl.TMPLAT_DEF_REG_AJAX, SiteUrl.TMPLAT_DEF_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrReqstRegAjax(
            final @Valid TmplatDefDto tmplatDefDto,
            final @Nullable Integer tmplatDefNo,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = tmplatDefNo == null;
            TmplatDefDto result = isReg ? tmplatDefService.regist(tmplatDefDto, request) : tmplatDefService.modify(tmplatDefDto, tmplatDefNo, request);
            isSuccess = (result.getTmplatDefNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(tmplatDefDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
