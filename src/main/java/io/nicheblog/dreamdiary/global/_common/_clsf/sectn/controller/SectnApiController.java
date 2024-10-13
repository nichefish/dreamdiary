package io.nicheblog.dreamdiary.global._common._clsf.sectn.controller;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common._clsf.sectn.model.SectnDto;
import io.nicheblog.dreamdiary.global._common._clsf.sectn.model.SectnParam;
import io.nicheblog.dreamdiary.global._common._clsf.sectn.model.SectnSearchParam;
import io.nicheblog.dreamdiary.global._common._clsf.sectn.service.SectnService;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.Optional;

/**
 * SectnController
 * <pre>
 *  단락 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class SectnApiController
        extends BaseControllerImpl {

    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.DEFAULT;      // 작업 카테고리 (로그 적재용)

    private final SectnService sectnService;

    /**
     * 단락 목록 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @GetMapping(Url.SECTN_LIST_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> sectnListAjax(
            @ModelAttribute("searchParam") SectnSearchParam searchParam,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            final Sort sort = Sort.by(Sort.Direction.ASC, "state.sortOrdr");
            final PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, sort);
            // 목록 조회 및 응답에 추가
            final Page<SectnDto> sectnList = sectnService.getPageDto(searchParam, pageRequest);
            ajaxResponse.setRsltList(sectnList.getContent());

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (NumberFormatException e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(new InvalidParameterException("파라미터 형식이 맞지 않습니다."));
            logParam.setExceptionInfo(e);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            ActvtyCtgr actvty = Optional.ofNullable(searchParam.getActvtyCtgrCd()).map(ActvtyCtgr::valueOf).orElse(null);
            logParam.setResult(isSuccess, rsltMsg, actvty);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 단락 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param sectn 등록/수정 처리할 객체
     * @param key 식별자
     * @param param 등록 파라미터
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param request - Multipart 요청
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(value = {Url.SECTN_REG_AJAX, Url.SECTN_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> sectnRegAjax(
            final @Valid SectnDto sectn,
            final @RequestParam("postNo") @Nullable Integer key,
            final SectnParam param,
            final MultipartHttpServletRequest request,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 등록 및 수정 처리
            final boolean isReg = (key == null);
            final SectnDto result = isReg ? sectnService.regist(sectn, request) : sectnService.modify(sectn, request);
            ajaxResponse.setRsltObj(result);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, param.getActvtyCtgr());
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }


    /**
     * 단락 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @GetMapping(Url.SECTN_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> sectnDtlAjax(
            final LogActvtyParam logParam,
            final SectnParam param,
            final @RequestParam("postNo") Integer key
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
            final SectnDto rsDto = sectnService.getDtlDto(key);
            ajaxResponse.setRsltObj(rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, param.getActvtyCtgr());
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
    
    /**
     * 단락 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param param 조회 파라미터
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.SECTN_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> sectnDelAjax(
            final LogActvtyParam logParam,
            final SectnParam param,
            final @RequestParam("postNo") Integer key
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
            isSuccess = sectnService.delete(key);
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, param.getActvtyCtgr());
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 관리자 > 메뉴 관리 > 정렬 순서 저장 (드래그앤드랍 결과 반영) (Ajax)
     *
     * @param sectnParam 키+정렬 순서 목록을 담은 파라미터
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.SECTN_SORT_ORDR_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> sectnSortOrdrAjax(
            final @RequestBody SectnParam sectnParam,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = null;
        try {
            // 메뉴 정렬 순서 저장
            isSuccess = sectnService.sortOrdr(sectnParam.getSortOrdr());
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // logParam.setCn("key: " + menuNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
}
