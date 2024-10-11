package io.nicheblog.dreamdiary.api.jrnl.day.controller;

import io.nicheblog.dreamdiary.api.jrnl.day.model.JrnlDayApiDto;
import io.nicheblog.dreamdiary.api.jrnl.day.model.JrnlDayApiSearchParam;
import io.nicheblog.dreamdiary.api.jrnl.day.service.JrnlDayApiService;
import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * JrnlDayApiController
 * <pre>
 *  API:: 저널 일자 조회 API controller
 * </pre>
 *
 * @author nichefish
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")   // CORS 에러 해결 위한 조치
@RequiredArgsConstructor
@Log4j2
@Tag(name = "저널 일자 API", description = "잔디 메신저 API입니다.")
public class JrnlDayApiController
        extends BaseControllerImpl {

    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.JRNL;      // 작업 카테고리 (로그 적재용)

    private final JrnlDayApiService jrnlDayApiService;

    /**
     * API:: 저널 일자 목록 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @Operation(
            summary = "저널 일자 목록 조회",
            description = "저널 일자 목록을 조회한다."
    )
    @GetMapping(value = {Url.API_JRNL_DAY_LIST_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlDayListAjax(
            final JrnlDayApiSearchParam searchParam,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
            Sort sort = Sort.by(Sort.Direction.ASC, "jrnlDt");
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, sort);
            Page<JrnlDayApiDto> jrnlDayList = jrnlDayApiService.getPageDto(searchParamMap, pageRequest);
            ajaxResponse.setRsltList(jrnlDayList.getContent());
            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * API:: 저널 일자 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @Operation(
            summary = "저널 일자 목록 조회",
            description = "저널 일자 목록을 조회한다."
    )
    @GetMapping(value = {Url.API_JRNL_DAY_DTL_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlDayDtlAjax(
            JrnlDayApiSearchParam searchParam,
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            JrnlDayApiDto rslt = jrnlDayApiService.getDtlDto(key);
            ajaxResponse.setRsltObj(rslt);

            isSuccess = (rslt.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }


}
