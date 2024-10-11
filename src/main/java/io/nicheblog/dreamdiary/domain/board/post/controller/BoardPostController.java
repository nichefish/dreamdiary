package io.nicheblog.dreamdiary.domain.board.post.controller;

import io.nicheblog.dreamdiary.domain._core.cd.service.DtlCdService;
import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.domain._core.tag.event.TagProcEvent;
import io.nicheblog.dreamdiary.domain._core.tag.service.TagService;
import io.nicheblog.dreamdiary.domain._core.viewer.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.domain.board.def.model.BoardDefDto;
import io.nicheblog.dreamdiary.domain.board.def.service.BoardDefService;
import io.nicheblog.dreamdiary.domain.board.post.model.BoardPostDto;
import io.nicheblog.dreamdiary.domain.board.post.model.BoardPostKey;
import io.nicheblog.dreamdiary.domain.board.post.model.BoardPostSearchParam;
import io.nicheblog.dreamdiary.domain.board.post.service.BoardPostService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.model.PaginationInfo;
import io.nicheblog.dreamdiary.global.model.SiteAcsInfo;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;

/**
 * BoardPostController
 * <pre>
 *  게시판 게시물 컨트롤러.
 *  화면단에선 boardCd, 어플리케이션 단에선 contentType으로 사용
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class BoardPostController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.BOARD_POST_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.BOARD_POST;        // 작업 카테고리 (로그 적재용)

    private final BoardDefService boardDefService;
    private final BoardPostService boardPostService;
    private final DtlCdService dtlCdService;
    private final TagService tagService;


    /**
     * 게시판 게시물 목록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return String - 화면의 뷰 이름
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.BOARD_POST_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostList(
            @ModelAttribute("searchParam") BoardPostSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        String boardCd = searchParam.getBoardCd();
        model.addAttribute("boardCd", boardCd);

        /* 사이트 메뉴 설정 */
        BoardDefDto boardDef = boardDefService.getDtlDto(boardCd);
        SiteAcsInfo boardMenu = boardDefService.getBoardMenu(boardCd);
        model.addAttribute(Constant.SITE_MENU, boardMenu.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            searchParam = (BoardPostSearchParam) CmmUtils.Param.checkPrevSearchParam(baseUrl, searchParam);
            // 상단 고정 목록 조회
            model.addAttribute("postFxdList", boardPostService.getFxdList(boardCd));
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, "managt.managtDt", model);
            // 목록 조회
            Page<BoardPostDto.LIST> postList = boardPostService.getPageDto(searchParam, pageRequest);
            model.addAttribute("postList", postList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(postList));
            // 컨텐츠 타입에 맞는 태그 목록 조회
            model.addAttribute("tagList", tagService.getContentSpecificTagList(boardCd));
            // 코드 정보 모델에 추가
            model.addAttribute(Constant.POST_CTGR_CD, dtlCdService.getCdDtoListByClCd(boardDef.getCtgrClCd()));
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

        return "/view/board/post/board_post_list";
    }

    /**
     * 게시판 게시물 등록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param boardCd 게시판 코드
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return String - 화면의 뷰 이름
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.BOARD_POST_REG_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostRegForm(
            final @RequestParam("boardCd") String boardCd,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 게시판 정의 정보 조회 */
        model.addAttribute("boardCd", boardCd);

        /* 사이트 메뉴 설정 */
        BoardDefDto boardDef = boardDefService.getDtlDto(boardCd);
        SiteAcsInfo boardMenu = boardDefService.getBoardMenu(boardCd);
        model.addAttribute(Constant.SITE_MENU, boardMenu.setAcsPageInfo(Constant.PAGE_REG));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute("post", new BoardPostDto());         // 빈 객체 주입 (freemarker error prevention)
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_REG, true);           // 등록/수정 화면 플래그 세팅
            // 코드 정보 모델에 추가
            model.addAttribute(Constant.POST_CTGR_CD, dtlCdService.getCdDtoListByClCd(boardDef.getCtgrClCd()));
            dtlCdService.setCdListToModel(Constant.MDFABLE_CD, model);
            dtlCdService.setCdListToModel(Constant.JANDI_TOPIC_CD, model);
            // CmmUtils.setModelFlsysPath(model);

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

        return "/view/board/post/board_post_reg_form";
    }

    /**
     * 게시판 게시물 등록 전 미리보기 팝업 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param boardPost 작성 중인 게시물
     * @param boardCd 게시판 코드
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return String - 화면의 뷰 이름
     */
    @PostMapping(Url.BOARD_POST_REG_PREVIEW_POP)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostRegPreviewPop(
            final BoardPostDto boardPost,
            final @RequestParam("boardCd") String boardCd,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 게시판 정의 정보 조회 */
        model.addAttribute("boardCd", boardCd);

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 정보 모델에 추가
            boardPost.setMarkdownCn(CmmUtils.markdown(boardPost.getCn()));
            model.addAttribute("post", boardPost);

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

        return "/view/board/post/board_post_preview_pop";
    }

    /**
     * 게시판 게시물 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param boardPost 등록/수정 처리할 게시물
     * @param postKey 복합키 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param request - Multipart 요청
     * @return {@link ResponseEntity} -- 응답 객체
     */
    @PostMapping(value = {Url.BOARD_POST_REG_AJAX, Url.BOARD_POST_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardPostRegAjax(
            final @Valid BoardPostDto.DTL boardPost,
            final BoardPostKey postKey,
            final LogActvtyParam logParam,
            // final @RequestParam("jandiYn") @Nullable String jandiYn,
            // final @RequestParam("trgetTopic") @Nullable String trgetTopic,
            final MultipartHttpServletRequest request
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 등록/수정 처리
            boolean isReg = postKey.getPostNo() == null;
            BoardPostDto.DTL result = isReg ? boardPostService.regist(boardPost, request) : boardPostService.modify(boardPost, request);
            ajaxResponse.setRsltObj(result);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            if (isSuccess) {
                // 태그 처리 :: 메인 로직과 분리
                publisher.publishEvent(new TagProcEvent(this, result.getClsfKey(), boardPost.tag));
                // 조치자 추가 :: 메인 로직과 분리
                publisher.publishEvent(new ViewerAddEvent(this, result.getClsfKey()));
                // 잔디 메세지 발송 :: 메인 로직과 분리
                // if ("Y".equals(jandiYn)) {
                //     String jandiRsltMsg = notifyService.notifyBoardPostReg(trgetTopic, result, logParam);
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
            logParam.setCn(boardPost.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 게시판 게시물 상세 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param postKey 복합키 식별자
     * @param boardCd 게시판 코드
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link ResponseEntity} -- 응답 객체
     */
    @GetMapping(value = Url.BOARD_POST_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostDtl(
            final BoardPostKey postKey,
            final @RequestParam("boardCd") String boardCd,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 게시판 정의 정보 조회 */
        model.addAttribute("boardCd", boardCd);

        /* 사이트 메뉴 설정 */
        SiteAcsInfo boardMenu = boardDefService.getBoardMenu(boardCd);
        model.addAttribute(Constant.SITE_MENU, boardMenu.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            BoardPostDto rsDto = boardPostService.getDtlDto(postKey.getClsfKey());
            model.addAttribute("post", rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            boardPostService.hitCntUp(postKey.getClsfKey());
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + postKey.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/board/post/board_post_dtl";
    }

    /**
     * 게시판 게시물 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param postKey 복합키 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 응답 객체
     */
    @GetMapping(Url.BOARD_POST_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardPostDtlAjax(
            final BoardPostKey postKey,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            BoardPostDto rsDto = boardPostService.getDtlDto(postKey.getClsfKey());
            ajaxResponse.setRsltObj(rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            boardPostService.hitCntUp(postKey.getClsfKey());
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + postKey.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 게시판 게시물 수정 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param postKey 복합키 식별자
     * @param boardCd 게시판 코드
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return String - 화면의 뷰 이름
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(value = Url.BOARD_POST_MDF_FORM)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String boardPostMdfForm(
            final BoardPostKey postKey,
            final @RequestParam("boardCd") String boardCd,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 게시판 정의 정보 조회 */
        model.addAttribute("boardCd", boardCd);

        /* 사이트 메뉴 설정 */
        BoardDefDto boardDef = boardDefService.getDtlDto(boardCd);
        SiteAcsInfo boardMenu = boardDefService.getBoardMenu(boardCd);
        model.addAttribute(Constant.SITE_MENU, boardMenu.setAcsPageInfo(Constant.PAGE_MDF));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            BoardPostDto rsDto = boardPostService.getDtlDto(postKey.getClsfKey());
            model.addAttribute("post", rsDto);
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_MDF, true);
            // 코드 정보 모델에 추가
            model.addAttribute(Constant.POST_CTGR_CD, dtlCdService.getCdDtoListByClCd(boardDef.getCtgrClCd()));
            dtlCdService.setCdListToModel(Constant.MDFABLE_CD, model);
            dtlCdService.setCdListToModel(Constant.JANDI_TOPIC_CD, model);
            // CmmUtils.setModelFlsysPath(model);

            isSuccess = rsDto.getPostNo() != null;
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl + "?boardCd=" + boardCd);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + postKey.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/board/post/board_post_reg_form";
    }

    /**
     * 게시판 게시물 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param postKey 복합키 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @PostMapping(Url.BOARD_POST_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardPostDelAjax(
            final BoardPostKey postKey,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
            isSuccess = boardPostService.delete(postKey.getClsfKey());
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            if (isSuccess) {
                // 태그 처리 :: 메인 로직과 분리
                publisher.publishEvent(new TagProcEvent(this, postKey.getClsfKey()));
            }
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + postKey.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
}
