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
import io.nicheblog.dreamdiary.global.util.CmmUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.Map;

/**
 * DreamController
 * <pre>
 *  API:: 꿈 일자 API controller
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 * TODO: 외부 호출시 토큰 인증 추가하기
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")   // CORS 에러 해결 위한 조치
@Log4j2
@Tag(name = "잔디 메신저 API", description = "잔디 메신저 API입니다.")
public class DreamDayApiController
        extends BaseControllerImpl {

    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.DREAM;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "dreamDayApiService")
    private DreamDayApiService dreamDayApiService;

    /**
     * 꿈 일자 목록 조회 (Ajax)
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
            final DreamDayApiSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Map<String, Object> searchParamMap = CmmUtils.convertParamToMap(searchParam);
            Sort sort = Sort.by(Sort.Direction.ASC, "dreamtDt");
            PageRequest pageRequest = CmmUtils.getPageRequest(searchParamMap, sort, model);
            Page<DreamDayApiDto> dreamDayList = dreamDayApiService.getListDto(searchParamMap, pageRequest);
            ajaxResponse.setResultList(dreamDayList.getContent());
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 꿈 일자 - 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @Operation(
            summary = "꿈 일자 등록/수정",
            description = "꿈 일자 정보를 등록/수정한다."
    )
    @PostMapping(value = {ApiUrl.API_DREAM_DAY_REG_AJAX, ApiUrl.API_DREAM_DAY_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dreamDayRegAjax(
            final @Valid DreamDayApiDto dreamDay,
            final Integer userNo,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = dreamDay.getDreamDayNo() == null;
            DreamDayApiDto result = isReg ? dreamDayApiService.regist(dreamDay, request) : dreamDayApiService.modify(dreamDay, userNo, request);
            isSuccess = (result.getDreamDayNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(dreamDay.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

}
