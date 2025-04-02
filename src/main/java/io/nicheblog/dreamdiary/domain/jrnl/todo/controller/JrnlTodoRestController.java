package io.nicheblog.dreamdiary.domain.jrnl.todo.controller;

import io.nicheblog.dreamdiary.domain.jrnl.todo.model.JrnlTodoDto;
import io.nicheblog.dreamdiary.domain.jrnl.todo.model.JrnlTodoSearchParam;
import io.nicheblog.dreamdiary.domain.jrnl.todo.service.JrnlTodoService;
import io.nicheblog.dreamdiary.extension.clsf.tag.handler.TagProcEventListener;
import io.nicheblog.dreamdiary.extension.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.extension.log.actvty.aspect.LogActvtyRestControllerAspect;
import io.nicheblog.dreamdiary.extension.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.util.List;

/**
 * JrnlTodoRestController
 * <pre>
 *  저널 할일 RestController.
 * </pre>
 *
 * @author nichefish
 * @see LogActvtyRestControllerAspect
 */
@RestController
@RequiredArgsConstructor
public class JrnlTodoRestController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.JRNL_DAY_PAGE;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.JRNL;        // 작업 카테고리 (로그 적재용)

    private final JrnlTodoService jrnlTodoService;

    /**
     * 저널 할일 목록 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(value = {Url.JRNL_TODO_LIST_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlTodoListAjax(
            final JrnlTodoSearchParam searchParam,
            final LogActvtyParam logParam
    ) throws Exception {

        final List<JrnlTodoDto> jrnlTodoList = jrnlTodoService.getListDtoWithCache(searchParam);
        final boolean isSuccess = true;
        final String rsltMsg = MessageUtils.RSLT_SUCCESS;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg);

        return ResponseEntity.ok(AjaxResponse.withAjaxResult(isSuccess, rsltMsg).withList(jrnlTodoList));
    }

    /**
     * 저널 할일 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param jrnlTodo 등록/수정 처리할 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param request - Multipart 요청
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     * @see TagProcEventListener
     */
    @Operation(
            summary = "저널 꿈 등록/수정",
            description = "저널 꿈 정보를 등록/수정한다."
    )
    @PostMapping(value = {Url.JRNL_TODO_REG_AJAX, Url.JRNL_TODO_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlTodoRegAjax(
            final @Valid JrnlTodoDto jrnlTodo,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request
    ) throws Exception {

        final boolean isReg = (jrnlTodo.getKey() == null);
        final ServiceResponse result = isReg ? jrnlTodoService.regist(jrnlTodo, request) : jrnlTodoService.modify(jrnlTodo, request);
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg);

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }

    /**
     * 저널 할일 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(value = {Url.JRNL_TODO_DTL_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlTodoDtlAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) throws Exception {

        final JrnlTodoDto retrievedDto = jrnlTodoService.getDtlDtoWithCache(key);
        final boolean isSuccess = (retrievedDto.getPostNo() != null);
        final String rsltMsg = MessageUtils.RSLT_SUCCESS;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg);

        return ResponseEntity.ok(AjaxResponse.withAjaxResult(isSuccess, rsltMsg).withObj(retrievedDto));
    }

    /**
     * 저널 할일 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param postNo 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     * @see TagProcEventListener
     */
    @PostMapping(value = {Url.JRNL_TODO_DEL_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlTodoDelAjax(
            final @RequestParam("postNo") Integer postNo,
            final LogActvtyParam logParam
    ) throws Exception {

        final ServiceResponse result = jrnlTodoService.delete(postNo);
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = MessageUtils.RSLT_SUCCESS;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg);

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }
}