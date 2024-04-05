package io.nicheblog.dreamdiary.web.controller.cmm.comment;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentParam;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentSearchParam;
import io.nicheblog.dreamdiary.web.service.cmm.comment.CommentService;
import lombok.Getter;
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

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;

/**
 * CommentController
 * <pre>
 *  댓글 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class CommentController
        extends BaseControllerImpl {

    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.DREAM;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "commentService")
    private CommentService commentService;

    /**
     * 댓글 목록 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.COMMENT_LIST_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> commentListAjax(
            @ModelAttribute("searchParam") CommentSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            Sort sort = Sort.by(Sort.Direction.ASC, "regDt");
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, sort, model);
            // 목록 조회 및 응답에 추가
            Page<CommentDto.LIST> commentList = commentService.getPageDto(searchParam, pageRequest);
            ajaxResponse.setResultList(commentList.getContent());

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (NumberFormatException e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(new InvalidParameterException("파라미터 형식이 맞지 않습니다."));
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, ActvtyCtgr.valueOf(searchParam.getActvtyCtgrCd()));
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 댓글 등록/수정 처리 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {SiteUrl.COMMENT_REG_AJAX, SiteUrl.COMMENT_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> commentRegAjax(
            final @Valid CommentDto.DTL commentDto,
            final @RequestParam("postNo") @Nullable Integer postNo,
            final CommentParam param,
            final LogActvtyParam logParam,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // Validation
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            // 등록 및 수정 처리
            boolean isReg = (postNo == null);
            CommentDto result = isReg ? commentService.regist(commentDto) : commentService.modify(commentDto, postNo);

            isSuccess = (result.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, param.getActvtyCtgr());
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 댓글 삭제 처리 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.COMMENT_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> commentDelAjax(
            final LogActvtyParam logParam,
            final CommentParam param,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 삭제 처리
            isSuccess = commentService.delete(key);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, resultMsg, param.getActvtyCtgr());
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
