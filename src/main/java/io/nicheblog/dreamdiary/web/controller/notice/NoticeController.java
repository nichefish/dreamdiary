package io.nicheblog.dreamdiary.web.controller.notice;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.exception.FailureException;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.CmmUtils;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeListDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeSearchParam;
import io.nicheblog.dreamdiary.web.service.notice.NoticeService;
import lombok.extern.log4j.Log4j2;
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
 * NoticeController
 * <pre>
 *  공지사항 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class NoticeController
        extends BaseControllerImpl {

    private final String baseUrl = SiteUrl.NOTICE_LIST;             // 기본 URL
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.NOTICE;      // 작업 카테고리 (로그 적재용)

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return actvtyCtgr.name();
    }

    @Resource(name = "noticeService")
    private NoticeService noticeService;

    // @Resource(name = "boardPostViewerService")
    // private BoardPostViewerService boardPostViewerService;
//
    // @Resource(name = "boardPostManagtrService")
    // private BoardPostManagtrService boardPostManagtrService;
//
    // @Resource(name = "boardTagService")
    // private BoardTagService boardTagService;
//
    // @Resource(name = "notifyService")
    // private NotifyService notifyService;

    @Resource(name = "cdService")
    private CdService cdService;

    /**
     * 공지사항 목록 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(SiteUrl.NOTICE_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String noticeList(
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") NoticeSearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.NOTICE.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            Map<String, Object> listParamMap = CmmUtils.checkPrevSearchMap(searchParamMap, baseUrl, searchParam);

            // 상단 고정 목록 조회
            model.addAttribute("noticeFxdList", noticeService.getFxdList());
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, "managt.managtDt", model);
            Page<NoticeListDto> noticeList = noticeService.getListDto(listParamMap, pageRequest);
            if (noticeList != null) model.addAttribute("noticeList", noticeList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(noticeList));
            cdService.setModelCdData(Constant.NOTICE_CTGR_CD, model);
            // 태그 전체 목록 조회
            // listParamMap.put("contentType", "notice");
            // Page<BoardTagDto> tagList = boardTagService.getListDto(listParamMap, Pageable.unpaged());
            // model.addAttribute("tagList", tagList.getContent());
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

        return "/view/notice/notice_list";
    }

    /**
     * 공지사항 등록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.NOTICE_REG_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String noticeRegForm(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.NOTICE.setAcsPageInfo(Constant.PAGE_REG));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            model.addAttribute("post", new NoticeDto());      // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute(Constant.IS_REG, true);           // 등록/수정 화면 플래그 세팅
            cdService.setModelCdData(Constant.NOTICE_CTGR_CD, model);
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
            // cmmService.setModelFlsysPath(model);
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

        return "/view/notice/notice_reg_form";
    }

    /**
     * 공지사항 등록 전 미리보기 팝업 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.NOTICE_REG_PREVIEW_POP)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String noticeRegPreviewPop(
            final NoticeDto noticeDto,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        model.addAttribute(Constant.SITE_MENU, SiteMenu.NOTICE.setAcsPageInfo("공지사항 미리보기"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // TODO: 파일 정보는? 난해하다...
            model.addAttribute("post", noticeDto);
            model.addAttribute("currDateStr", DateUtils.getCurrDateStr(DateUtils.PTN_DATETIME));
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            // 로그 관련 처리
            logParam.setCn(noticeDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/notice/notice_reg_preview_pop";
    }

    /**
     * 공지사항 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {SiteUrl.NOTICE_REG_AJAX, SiteUrl.NOTICE_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeRegAjax(
            final @Valid NoticeDto noticeDto,
            final @RequestParam("postNo") @Nullable Integer key,
            final @RequestParam("jandiYn") @Nullable String jandiYn,
            final @RequestParam("trgetTopic") @Nullable String trgetTopic,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = (key == null);
            NoticeDto result = isReg ? noticeService.regist(noticeDto, request) : noticeService.modify(noticeDto, key, request);
            isSuccess = (result.getPostNo() != null);
            if (!isSuccess) throw new FailureException("처리에 실패했습니다.");
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조치자 목록 갱신 :: 메인 로직과 분리
            // try {
            //     List<BoardPostManagtrDto> managtrList = result.getManagtrList();
            //     if (!boardPostManagtrService.hasAlreadyManagt(managtrList)) {
            //         BoardPostManagtrDto dto = boardPostManagtrService.regPostManagtr(result.getClsfKey());
            //         result.addPostManagtr(dto);
            //     }
            // } catch (Exception e) {
            //     resultMsg = MessageUtils.getExceptionMsg(e);
            //     logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            //     publisher.publishEvent(new LogActvtyEvent(this, logParam));
            // }
            // 잔디 메세지 발송 :: 메인 로직과 분리
            // if ("Y".equals(jandiYn)) {
            //     String jandiResultMsg = notifyService.notifyNoticeReg(trgetTopic, result, logParam);
            //     resultMsg = resultMsg + "\n" + jandiResultMsg;
            // }
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(noticeDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 공지사항 상세 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(value = SiteUrl.NOTICE_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String noticeDtl(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.NOTICE.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            NoticeDto rsDto = noticeService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            // 열람자 목록 및 조회수 카운트 추가 :: 메인 로직과 분리
            // try {
            //     List<BoardPostViewerDto> viewerList = rsDto.getViewerList();
            //     if (!boardPostViewerService.hasAlreadyView(viewerList)) {
            //         BoardPostViewerDto dto = boardPostViewerService.regPostViewer(postKey);
            //         rsDto.addPostViewer(dto);
            //     }
            //     noticeService.hitCntUp(postKey);
            // } catch (Exception e) {
            //     logParam.setResult(false, MessageUtils.getExceptionMsg(e));
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
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/notice/notice_dtl";
    }

    /**
     * 공지사항 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.NOTICE_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeDtlAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 게시판 정보 조회
            NoticeDto rsDto = noticeService.getDtlDto(key);
            // 열람자 목록 및 조회수 카운트 추가
            // try {
            //     List<BoardPostViewerDto> viewerList = rsDto.getViewerList();
            //     if (!boardPostViewerService.hasAlreadyView(viewerList)) {
            //         BoardPostViewerDto dto = boardPostViewerService.regPostViewer(postKey);
            //         rsDto.addPostViewer(dto);
            //     }
            //     noticeService.hitCntUp(postKey);
            // } catch (Exception e) {
            //     logParam.setResult(false, MessageUtils.getExceptionMsg(e));
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
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 공지사항 수정 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(value = SiteUrl.NOTICE_MDF_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String noticeMdfForm(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.NOTICE.setAcsPageInfo(Constant.PAGE_MDF));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            NoticeDto rsDto = noticeService.getDtlDto(key);
            model.addAttribute("post", rsDto);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 모델 정보 추가
            model.addAttribute(Constant.IS_MDF, true);
            cdService.setModelCdData(Constant.NOTICE_CTGR_CD, model);
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
            // cmmService.setModelFlsysPath(model);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/notice/notice_reg_form";
    }

    /**
     * 공지사항 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.NOTICE_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeDelAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = noticeService.delete(key);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 공지사항 팝업공지 목록 조회 (Ajax)
     * 비로그인 사용자도 외부에서 접근 가능 (인증 없음)
     */
    @RequestMapping(SiteUrl.NOTICE_POPUP_LIST_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticePopupListAjax(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 팝업공지 목록 조회
            Map<String, Object> searchParamMap = new HashMap<>() {{
                put("popupYn", "Y");
                put("managtStartDt", DateUtils.getCurrDateAddDay(-7));
            }};
            Sort sort = Sort.by(Sort.Direction.ASC, "managtDt");
            PageRequest pageRequest = CmmUtils.getPageRequest(searchParamMap, sort, model);
            Page<NoticeListDto> noticeList = noticeService.getListDto(searchParamMap, pageRequest);
            ajaxResponse.setResultList(noticeList.getContent());
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 반복적으로 호출되므로 실패(Exception)시 외에는 로그 적재하지 않음
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

}