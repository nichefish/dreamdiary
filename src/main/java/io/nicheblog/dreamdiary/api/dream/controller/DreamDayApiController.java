package io.nicheblog.dreamdiary.api.dream.controller;

import io.nicheblog.dreamdiary.api.ApiUrl;
import io.nicheblog.dreamdiary.api.dream.model.DreamDayApiDto;
import io.nicheblog.dreamdiary.api.dream.model.DreamDayApiSearchParam;
import io.nicheblog.dreamdiary.api.dream.service.DreamDayApiService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * DreamDayApiController
 * <pre>
 *  API:: 꿈 일자 조회 API controller
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 * TODO: 외부 호출시 토큰 인증 추가하기
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")   // CORS 에러 해결 위한 조치
@Log4j2
@Tag(name = "꿈 일자 API", description = "잔디 메신저 API입니다.")
public class DreamDayApiController
        extends BaseControllerImpl {

    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.DREAM;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "dreamDayApiService")
    private DreamDayApiService dreamDayApiService;

    /**
     * API:: 꿈 일자 목록 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @Operation(
            summary = "꿈 일자 목록 조회",
            description = "꿈 일자 목록을 조회한다."
    )
    @GetMapping(value = {ApiUrl.API_DREAM_DAY_LIST_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dreamDayListAjax(
            DreamDayApiSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
            Sort sort = Sort.by(Sort.Direction.ASC, "dreamtDt");
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, sort, model);
            Page<DreamDayApiDto> dreamDayList = dreamDayApiService.getPageDto(searchParamMap, pageRequest);
            ajaxResponse.setRsltList(dreamDayList.getContent());
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

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * API:: 꿈 일자 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @Operation(
            summary = "꿈 일자 목록 조회",
            description = "꿈 일자 목록을 조회한다."
    )
    @GetMapping(value = {ApiUrl.API_DREAM_DAY_DTL_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dreamDayDtlAjax(
            DreamDayApiSearchParam searchParam,
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            DreamDayApiDto rslt = dreamDayApiService.getDtlDto(key);
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

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }


}
