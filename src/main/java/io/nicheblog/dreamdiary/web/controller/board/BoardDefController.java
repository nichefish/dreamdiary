package io.nicheblog.dreamdiary.web.controller.board;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.board.BoardDefDto;
import io.nicheblog.dreamdiary.web.model.board.BoardDefSearchParam;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.service.board.BoardDefService;
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
 * BoardDefController
 * <pre>
 *  게시판 정의 관리 컨트롤러
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class BoardDefController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.BOARD_DEF_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.BOARD_DEF;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "boardDefService")
    private BoardDefService boardDefService;

    @Resource(name = "cdService")
    private CdService cdService;

    /**
     * 게시판 정의 목록 조회
     * (관리자MNGR만 접근 가능)
     */
    @GetMapping(SiteUrl.BOARD_DEF_LIST)
    @Secured({Constant.ROLE_MNGR})
    public String boardDefList(
            @ModelAttribute("searchParam") BoardDefSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.BOARD_DEF.setAcsPageInfo("게시판 관리"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            String baseUrl = SiteUrl.BOARD_DEF_LIST;
            searchParam = (BoardDefSearchParam) CmmUtils.Param.checkPrevSearchParam(baseUrl, searchParam);

            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, "sortOrdr", model);
            Page<BoardDefDto> boardDefMngList = boardDefService.getListDto(searchParam, pageRequest);
            if (boardDefMngList != null) model.addAttribute("boardDefMngList", boardDefMngList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(boardDefMngList));
            cdService.setModelCdData(Constant.POST_CD, model);

            CmmUtils.Param.setModelAttrMap(searchParam, baseUrl, model);        // 검색 파라미터 다시 모델에 추가

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

        return "/view/board/def/board_def_list";
    }

    /**
     * 게시판 정의 등록 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.BOARD_DEF_REG_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardDefRegAjax(
            final @Valid BoardDefDto boardDefDto,
            final LogActvtyParam logParam,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            BoardDefDto result = boardDefService.regist(boardDefDto);
            isSuccess = (result.getBoardCd() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(boardDefDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 게시판 정의 항목 수정 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.BOARD_DEF_MDF_ITEM_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardDefMdfItemAjax(
            final @Valid BoardDefDto boardDefDto,
            final String boardCd,
            final LogActvtyParam logParam,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            BoardDefDto result = boardDefService.modify(boardDefDto, boardCd);
            isSuccess = (result.getBoardCd() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(boardDefDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 게시판 정의 사용 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.BOARD_DEF_USE_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardDefUseAjax(
            final @RequestParam("boardCd") String boardCd,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = boardDefService.setInUse(boardCd);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + boardCd);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 게시판 정의 미사용 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.BOARD_DEF_UNUSE_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardDefUnuseAjax(
            final @RequestParam("boardCd") String boardCd,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = boardDefService.setInUnuse(boardCd);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + boardCd);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 게시판 정의 삭제 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.BOARD_DEF_DEL_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardDefDelAjax(
            final @RequestParam("boardCd") String boardCd,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = boardDefService.delete(boardCd);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + boardCd);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
