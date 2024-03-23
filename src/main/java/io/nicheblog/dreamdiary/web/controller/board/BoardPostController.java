/*
package io.nicheblog.dreamdiary.web.controller.board;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostKey;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.SiteMenuAcsInfo;
import lombok.extern.log4j.Log4j2;
import dreamdiary.nicheblog.io.cmm.Constant;
import dreamdiary.nicheblog.io.cmm.event.LogActvtyEvent;
import dreamdiary.nicheblog.io.cmm.exception.FailureException;
import dreamdiary.nicheblog.io.cmm.intrfc.controller.impl.BaseControllerImpl;
import dreamdiary.nicheblog.io.cmm.util.MessageUtils;
import dreamdiary.nicheblog.io.web.entity.BasePostKey;
import dreamdiary.nicheblog.io.web.model.PaginationInfo;
import dreamdiary.nicheblog.io.web.model.board.*;
import dreamdiary.nicheblog.io.web.model.user.UserCttpcListDto;
import dreamdiary.nicheblog.io.web.service.CmmCdService;
import dreamdiary.nicheblog.io.web.service.CmmService;
import dreamdiary.nicheblog.io.web.service.NotifyService;
import dreamdiary.nicheblog.io.web.service.board.*;
import dreamdiary.nicheblog.io.web.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

*/
/**
 * BoardPostController
 * <pre>
 *  게시판 게시물 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 *//*

@Controller
@Log4j2
public class BoardPostController
        extends BaseControllerImpl {

    private final String baseUrl = SiteUrl.BOARD_POST_LIST;
    // 작업 카테고리 (로그 적재용)
    private final String actvtyCtgrCd = Constant.ACTVTY_BOARD_POST;

    @Resource(name = "boardDefService")
    private BoardDefService boardDefService;

    @Resource(name = "boardPostService")
    private BoardPostService boardPostService;

    @Resource(name = "boardPostViewerService")
    private BoardPostViewerService boardPostViewerService;

    @Resource(name = "boardPostManagtrService")
    private BoardPostManagtrService boardPostManagtrService;

    @Resource(name = "boardTagService")
    private BoardTagService boardTagService;

    @Resource(name = "cmmCdService")
    public CmmCdService cmmCdService;

    @Resource(name = "notifyService")
    private NotifyService notifyService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "xlsxUtils")
    private XlsxUtils xlsxUtils;

    */
/**
     * 게시판 게시물 목록 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @GetMapping(SiteUrl.BOARD_POST_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostList(
            final @ModelAttribute(Constant.SITE_MENU) SiteMenuAcsInfo siteMenuAcsInfo,
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") BoardPostSearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap,
            final @RequestParam("boardCd") String boardCd,
            final ModelMap model
    ) throws Exception {

        model.addAttribute("boardCd", boardCd);
        */
/* 사이트 메뉴 설정 *//*

        BoardDefDto boardDef = boardDefService.getDtlDto(boardCd);
        siteMenuAcsInfo.setAcsInfo(boardDef, SiteMenu.PAGE_LIST, request.getRequestURI());

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            Map<String, Object> listParamMap = cmmService.checkPrevSearchMap(searchParamMap, baseUrl, searchParam);

            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            // "프로젝트 세미나"는 등록일순, 나머지는 최종수정일순 정렬
            String sortParam = "pjtSemina".equals(boardCd) ? "regDt" : "managtDt";
            PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, sortParam, model);
            Page<BoardPostListDto> postList = boardPostService.getListDto(listParamMap, pageRequest);
            if (postList != null) model.addAttribute("postList", postList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(postList));
            model.addAttribute(Constant.POST_CTGR_CD, cmmCdService.getCdListByClCd(boardDef.getCtgrClCd()));
            // 상단 고정 목록 조회
            List<BoardPostListDto> postFxdList = boardPostService.getFxdList(boardCd);
            model.addAttribute("postFxdList", postFxdList);
            // 슬기로운 회사생활 게시판일 경우 직원연락처 목록 조회
            String currDateStr = DateUtils.getCurrDateStr(DateUtils.PTN_DATE);
            List<UserCttpcListDto> crtdUserCttpcList = userService.getCrdtUserCttpcList(currDateStr, currDateStr);
            model.addAttribute("crtdUserCttpcList", crtdUserCttpcList);
            // 태그 전체 목록 조회
            Page<BoardTagDto> tagList = boardTagService.getListDto(listParamMap, Pageable.unpaged());
            model.addAttribute("tagList", tagList.getContent());
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
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/board/post/board_post_list";
    }

    */
/**
     * 게시판 게시물 등록 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @RequestMapping(SiteUrl.BOARD_POST_REG_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostRegForm(
            final @ModelAttribute(Constant.SITE_MENU) SiteMenuAcsInfo siteMenuAcsInfo,
            final LogActvtyParam logParam,
            final @RequestParam("boardCd") String boardCd,
            final ModelMap model
    ) throws Exception {

        */
/* 게시판 정의 정보 조회 *//*

        model.addAttribute("boardCd", boardCd);
        */
/* 사이트 메뉴 설정 *//*

        BoardDefDto boardDef = boardDefService.getDtlDto(boardCd);
        siteMenuAcsInfo.setAcsInfo(boardDef, SiteMenu.PAGE_REG, request.getRequestURI());

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            model.addAttribute("post", new BoardPostDto());         // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute(Constant.IS_REG, true);           // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.POST_CTGR_CD, cmmCdService.getCdListByClCd(boardDef.getCtgrClCd()));
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
            cmmService.setModelFlsysPath(model);

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/board/post/board_post_reg_form";
    }

    */
/**
     * 게시판 게시물 등록 전 미리보기 팝업 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @RequestMapping(SiteUrl.BOARD_POST_REG_PREVIEW_POP)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostRegPreviewPop(
            final BoardPostDto boardPostDto,
            final LogActvtyParam logParam,
            final @RequestParam("boardCd") String boardCd,
            final ModelMap model
    ) {

        */
/* 게시판 정의 정보 조회 *//*

        model.addAttribute("boardCd", boardCd);

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 태그를 먼저 처리해준다.
            String tagListStr = boardPostDto.getTagListStr();
            List<BoardTagDto> tagList = boardTagService.parseTagList(tagListStr);
            if (!CollectionUtils.isEmpty(tagList)) {
                List<BoardPostTagDto> postTagList = new ArrayList<>();
                for (BoardTagDto tag : tagList) {
                    postTagList.add(new BoardPostTagDto(tag.getBoardTag()));
                }
                boardPostDto.setTagList(postTagList);
            }
            isSuccess = true;
            // TODO: 파일 정보는? 난해하다...
            model.addAttribute("post", boardPostDto);
            model.addAttribute("currDateStr", DateUtils.getCurrDateStr(DateUtils.PTN_DATETIME));
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/board/post/board_post_reg_preview_pop";
    }

    */
/**
     * 게시판 게시물 등록/수정 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @PostMapping(value = {SiteUrl.BOARD_POST_REG_AJAX, SiteUrl.BOARD_POST_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardPostRegAjax(
            final @Valid BoardPostDto boardPostDto,
            final LogActvtyParam logParam,
            final BasePostKey key,
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
            boolean isReg = key.getPostNo() == null;
            BoardPostDto result = isReg ? boardPostService.regist(boardPostDto, request) : boardPostService.modify(boardPostDto, key, request);
            isSuccess = (result.getPostNo() != null);
            if (!isSuccess) throw new FailureException("처리에 실패했습니다.");
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조치자 목록 갱신 :: 메인 로직과 분리
            try {
                List<BoardPostManagtrDto> managtrList = result.getManagtrList();
                if (!boardPostManagtrService.hasAlreadyManagt(managtrList)) {
                    BoardPostManagtrDto dto = boardPostManagtrService.regPostManagtr(result.getPostKey());
                    result.addPostManagtr(dto);
                }
            } catch (Exception e) {
                resultMsg = MessageUtils.getExceptionMsg(e);
                logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
                publisher.publishEvent(new LogActvtyEvent(this, logParam));
            }
            // 잔디 메세지 발송 :: 메인 로직과 분리
            if ("Y".equals(jandiYn)) {
                String jandiResultMsg = notifyService.notifyBoardPostReg(trgetTopic, result, logParam);
                resultMsg = resultMsg + "\n" + jandiResultMsg;
            }
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(boardPostDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    */
/**
     * 게시판 게시물 상세 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @RequestMapping(value = SiteUrl.BOARD_POST_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostDtl(
            final @ModelAttribute(Constant.SITE_MENU) SiteMenuAcsInfo siteMenuAcsInfo,
            final LogActvtyParam logParam,
            final BasePostKey postKey,
            final @RequestParam("boardCd") String boardCd,
            final ModelMap model
    ) throws Exception {

        */
/* 게시판 정의 정보 조회 *//*

        model.addAttribute("boardCd", boardCd);
        */
/* 사이트 메뉴 설정 *//*

        BoardDefDto boardDef = boardDefService.getDtlDto(boardCd);
        siteMenuAcsInfo.setAcsInfo(boardDef, SiteMenu.PAGE_DTL, request.getRequestURI());

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            BoardPostDto rsDto = boardPostService.getDtlDto(postKey);
            model.addAttribute("post", rsDto);
            // 열람자 목록 및 조회수 카운트 추가
            try {
                List<BoardPostViewerDto> viewerList = rsDto.getViewerList();
                if (!boardPostViewerService.hasAlreadyView(viewerList)) {
                    BoardPostViewerDto dto = boardPostViewerService.regPostViewer(postKey);
                    rsDto.addPostViewer(dto);
                }
                boardPostService.hitCntUp(postKey);
            } catch (Exception e) {
                resultMsg = MessageUtils.getExceptionMsg(e);
                logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
                publisher.publishEvent(new LogActvtyEvent(this, logParam));
            }
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + postKey.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/board/post/board_post_dtl";
    }

    */
/**
     * 게시판 게시물 상세 조회 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @RequestMapping(SiteUrl.BOARD_POST_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardPostDtlAjax(
            final LogActvtyParam logParam,
            final BasePostKey postKey
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 게시판 정보 조회
            BoardPostDto rsDto = boardPostService.getDtlDto(postKey);
            // 열람자 목록 및 조회수 카운트 추가
            try {
                List<BoardPostViewerDto> viewerList = rsDto.getViewerList();
                if (!boardPostViewerService.hasAlreadyView(viewerList)) {
                    BoardPostViewerDto dto = boardPostViewerService.regPostViewer(postKey);
                    rsDto.addPostViewer(dto);
                }
                boardPostService.hitCntUp(postKey);
            } catch (Exception e) {
                resultMsg = MessageUtils.getExceptionMsg(e);
                logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
                publisher.publishEvent(new LogActvtyEvent(this, logParam));
            }
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
            logParam.setCn("key: " + postKey.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    */
/**
     * 게시판 게시물 수정 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @RequestMapping(value = SiteUrl.BOARD_POST_MDF_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostMdfForm(
            final @ModelAttribute(Constant.SITE_MENU) SiteMenuAcsInfo siteMenuAcsInfo,
            final LogActvtyParam logParam,
            final BasePostKey postKey,
            final @RequestParam("boardCd") String boardCd,
            final ModelMap model
    ) throws Exception {

        */
/* 게시판 정의 정보 조회 *//*

        BoardDefDto boardDef = boardDefService.getDtlDto(boardCd);
        model.addAttribute("boardCd", boardCd);
        */
/* 사이트 메뉴 설정 *//*

        siteMenuAcsInfo.setAcsInfo(boardDef, SiteMenu.PAGE_MDF, request.getRequestURI());

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            BoardPostDto rsDto = boardPostService.getDtlDto(postKey);
            isSuccess = rsDto.getPostNo() != null;
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            model.addAttribute("post", rsDto);

            model.addAttribute(Constant.IS_MDF, true);           // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.POST_CTGR_CD, cmmCdService.getCdListByClCd(boardDef.getCtgrClCd()));
            cdService.setModelCdData(Constant.MDFABLE_CD, model);
            cdService.setModelCdData(Constant.JANDI_TOPIC_CD, model);
            cmmService.setModelFlsysPath(model);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl + "?boardCd=" + boardCd);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + postKey.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/board/post/board_post_reg_form";
    }

    */
/**
     * 게시판 게시물 삭제 (Ajax)
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @PostMapping(SiteUrl.BOARD_POST_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardPostDelAjax(
            final LogActvtyParam logParam,
            final BasePostKey postKey
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = boardPostService.delete(postKey);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + postKey.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    */
/**
     * 직원 연락처 목록 엑셀 다운로드
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @RequestMapping(SiteUrl.CRDT_USER_CTTPC_LIST_XLSX_DOWNLOAD)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public void crdtUserCttpcListXlsxDownload(
            final LogActvtyParam logParam,
            final @RequestParam("boardCd") String boardCd
    ) throws Exception {

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            String currDateStr = DateUtils.getCurrDateStr(DateUtils.PTN_DATE);
            List<Object> userCttpcXlsxList = userService.getCrdtUserCttpcListXlsx(currDateStr, currDateStr);
            xlsxUtils.listXlxsDownload(Constant.CRDT_USER_CTTPC, userCttpcXlsxList);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl + "?boardCd=" + boardCd);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }
}
*/
