package io.nicheblog.dreamdiary.api.jandi.controller;

import io.nicheblog.dreamdiary.api.ApiUrl;
import io.nicheblog.dreamdiary.api.jandi.model.JandiApiRcvMsgDto;
import io.nicheblog.dreamdiary.api.jandi.model.JandiApiRespnsDto;
import io.nicheblog.dreamdiary.api.jandi.model.JandiParam;
import io.nicheblog.dreamdiary.api.jandi.service.JandiApiService;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * JandiApiController
 * <pre>
 *  API:: JANDI (incoming/outgoing) webhook 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")   // CORS 에러 해결 위한 조치
@Log4j2
@Tag(name = "잔디 메신저 API", description = "잔디 메신저 API입니다.")
public class JandiApiController
        extends BaseControllerImpl {

    @Resource(name = "jandiApiService")
    private JandiApiService jandiApiService;

    /**
     * JANDI :: 잔디 메신저로 웹훅 메세지 전송
     */
    @Operation(
            summary = "잔디 메신저로 웹훅 메세지 전송",
            description = "잔디 메신저로 웹훅 메세지를 전송한다."
    )
    @PostMapping(ApiUrl.API_JANDI_SND_MSG)
    public ResponseEntity<JandiApiRespnsDto> sendMsg(
            final LogActvtyParam logParam,
            final JandiParam jandiParam
    ) {

        JandiApiRespnsDto apiResponse = new JandiApiRespnsDto();

        log.info("requestUrl: {}, jandiParam: {}", request.getRequestURL(), jandiParam.toString());

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = jandiApiService.sendMsg(jandiParam);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            apiResponse.setApiResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /**
     * JANDI :: 잔디 메신저로부터 웹훅 메세지 수신
     */
    @Operation(
            summary = "잔디 메신저로부터 웹훅 메세지 수신",
            description = "잔디 메신저로부터 웹훅 메세지를 수신한다."
    )
    @PostMapping(ApiUrl.API_JANDI_RCV_MSG)
    public ResponseEntity<JandiApiRespnsDto> receiveMsg(
            final @RequestBody @Nullable JandiApiRcvMsgDto rcvMsg,
            final LogActvtyParam logParam
    ) {

        JandiApiRespnsDto apiResponse = new JandiApiRespnsDto();

        log.info("requestUrl: {}", request.getRequestURL());

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = jandiApiService.receiveMsg(rcvMsg);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            apiResponse.setApiResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
