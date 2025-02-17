package io.nicheblog.dreamdiary.domain.board.post.controller;

import io.nicheblog.dreamdiary.domain.board.post.model.BoardPostDto;
import io.nicheblog.dreamdiary.domain.board.post.service.BoardPostService;
import io.nicheblog.dreamdiary.extension.clsf.ContentType;
import io.nicheblog.dreamdiary.extension.clsf.tag.event.TagProcEvent;
import io.nicheblog.dreamdiary.extension.clsf.tag.handler.TagProcEventListener;
import io.nicheblog.dreamdiary.extension.clsf.viewer.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.extension.clsf.viewer.handler.ViewerEventListener;
import io.nicheblog.dreamdiary.extension.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.extension.log.actvty.aspect.LogActvtyRestControllerAspect;
import io.nicheblog.dreamdiary.extension.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;

/**
 * BoardPostRestController
 * <pre>
 *  게시판 게시물 API 컨트롤러.
 *  화면단에선 boardDef, 어플리케이션 단에선 contentType으로 사용
 * </pre>
 *
 * @author nichefish
 * @see LogActvtyRestControllerAspect
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class BoardPostRestController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.BOARD_POST_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.BOARD_POST;        // 작업 카테고리 (로그 적재용)

    private final BoardPostService boardPostService;

    /**
     * 게시판 게시물 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param boardPost 등록/수정 처리할 게시물
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param request - Multipart 요청
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     * @see TagProcEventListener ,ViewerEventListener
     */
    @PostMapping(value = {Url.BOARD_POST_REG_AJAX, Url.BOARD_POST_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardPostRegAjax(
            final @Valid BoardPostDto.DTL boardPost,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request
    ) throws Exception {

        final boolean isReg = (boardPost.getKey() == null);
        final ServiceResponse result = isReg ? boardPostService.regist(boardPost, request) : boardPostService.modify(boardPost, request);
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE;

        // TODO: AOP로 분리하기
        if (isSuccess) {
            final BoardPostDto rsltObj = (BoardPostDto) result.getRsltObj();
            // 조치자 추가 :: 메인 로직과 분리
            publisher.publishAsyncEvent(new ViewerAddEvent(this, rsltObj.getClsfKey()));
            // 태그 처리 :: 메인 로직과 분리
            publisher.publishAsyncEventAndWait(new TagProcEvent(this, rsltObj.getClsfKey(), boardPost.tag));
            // 잔디 메세지 발송 :: 메인 로직과 분리
            // if ("Y".equals(jandiYn)) {
            //     String jandiRsltMsg = notifyService.notifyBoardPostReg(trgetTopic, result, logParam);
            //     rsltMsg = rsltMsg + "\n" + jandiRsltMsg;
            // }
        }

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }

    /**
     * 게시판 게시물 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param postNo 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     * @see ViewerEventListener
     */
    @GetMapping(Url.BOARD_POST_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardPostDtlAjax(
            final @RequestParam("postNo") Integer postNo,
            final LogActvtyParam logParam
    ) throws Exception {

        final BoardPostDto retrievedDto = boardPostService.getDtlDto(postNo);
        final boolean isSuccess = true;
        final String rsltMsg = MessageUtils.RSLT_SUCCESS;

        // 조회수 카운트 추가
        // TODO: AOP로 분리
        boardPostService.hitCntUp(postNo);
        // 열람자 추가 :: 메인 로직과 분리
        // TODO: AOP로 분리
        publisher.publishAsyncEvent(new ViewerAddEvent(this, retrievedDto.getClsfKey()));

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);

        return ResponseEntity.ok(AjaxResponse.withAjaxResult(isSuccess, rsltMsg).withObj(retrievedDto));
    }

    /**
     * 게시판 게시물 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param postNo 복합키 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     * @see TagProcEventListener
     */
    @PostMapping(Url.BOARD_POST_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> boardPostDelAjax(
            final Integer postNo,
            final LogActvtyParam logParam
    ) throws Exception {

        final ServiceResponse result = boardPostService.delete(postNo);
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = MessageUtils.RSLT_SUCCESS;

        // TODO: AOP로 분리하기
        if (isSuccess) {
            // 태그 처리 :: 메인 로직과 분리
            publisher.publishAsyncEventAndWait(new TagProcEvent(this, new BaseClsfKey(postNo, ContentType.BOARD)));
        }

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }
}
