package io.nicheblog.dreamdiary.api.kasi.controller;

import io.nicheblog.dreamdiary.api.ApiUrl;
import io.nicheblog.dreamdiary.api.kasi.model.HldyKasiApiItemDto;
import io.nicheblog.dreamdiary.api.kasi.service.HldyKasiApiService;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;

/**
 * HldyKasiApiController
 * <pre>
 *  API:: 한국천문연구원(KASI):: 특일 정보 API 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")   // CORS 에러 해결 위한 조치
@Log4j2
@Tag(
        name = "한국천문연구원 특일 정보 API",
        description = "한국천문연구원 특일 정보 API입니다."
)
public class HldyKasiApiController
        extends BaseControllerImpl {

    @Resource(name = "hldyKasiApiService")
    private HldyKasiApiService hldyKasiApiService;

    /**
     * 한국천문연구원(KASI):: 휴일 정보 조회 및 DB 저장
     */
    @Operation(
            summary = "휴일 정보 조회 및 DB 저장",
            description = "잔디 메신저로부터 웹훅 메세지 수신한다."
    )
    @PostMapping(ApiUrl.API_HLDY_GET)
    public ResponseEntity<AjaxResponse> getHldyInfo(
            final @RequestParam("yy") @Nullable String yyParam,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        log.info("requestUrl: {}", request.getRequestURL() + "?" + request.getQueryString());

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            String yyStr = !StringUtils.isEmpty(yyParam) ? yyParam : DateUtils.getCurrYearStr();
            // 기존 정보 (API로 받아온 휴일) 삭제 후 재등록
            hldyKasiApiService.delHldyList(yyStr);
            List<HldyKasiApiItemDto> hldyApiList = hldyKasiApiService.getHldyList(yyStr);
            ajaxResponse.setResultList(hldyApiList);
            isSuccess = hldyKasiApiService.regHldyList(hldyApiList);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
