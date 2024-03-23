/*
package io.nicheblog.dreamdiary.web.controller.tag;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import lombok.extern.log4j.Log4j2;
import dreamdiary.nicheblog.io.cmm.event.LogActvtyEvent;
import dreamdiary.nicheblog.io.web.model.board.BoardTagDto;
import dreamdiary.nicheblog.io.web.model.board.BoardTagSearchParam;
import dreamdiary.nicheblog.io.web.service.board.BoardTagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

*/
/**
 * TagController
 * <pre>
 *  게시판 태그 관리 컨트롤러
 *  ※게시판 태그(board_tag) = 게시판별 태그 정보. 게시판 게시물 태그(board_post_tag)와 1:N으로 연관된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 *//*

@Controller
@Log4j2
public class TagController
        extends BaseControllerImpl {

    // 작업 카테고리 (로그 적재용)
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.TAG;

    @Resource(name = "tagService")
    private TagService tagService;

    */
/**
     * 게시판 태그 전체 목록 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @RequestMapping(SiteUrl.BOARD_TAG_LIST_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardTagListAjax(
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") BoardTagSearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 전체 태그 목록 조회 (태그클라우드)
            Page<BoardTagDto> tagList = boardTagService.getListDto(searchParamMap, Pageable.unpaged());
            ajaxResponse.setResultList(tagList.getContent());
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    */
/**
     * 게시판 태그별 글 목록 화면 조회
     * 사용자USER, 관리자MNGR만 접근 가능
     *//*

    @RequestMapping(SiteUrl.BOARD_TAG_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardTagDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("boardTag") String boardTag
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 태그 상세 조회 (관련글 목록 포함)
            BoardTagDto boardTagDto = boardTagService.getDtlDto(boardTag);
            ajaxResponse.setResultObj(boardTagDto);
            if (!CollectionUtils.isEmpty(boardTagDto.getPostList())) ajaxResponse.setResultList(boardTagDto.getPostList());
            if (!CollectionUtils.isEmpty(boardTagDto.getNoticeList())) ajaxResponse.setResultList(boardTagDto.getNoticeList());
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + boardTag);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgrCd);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}*/
