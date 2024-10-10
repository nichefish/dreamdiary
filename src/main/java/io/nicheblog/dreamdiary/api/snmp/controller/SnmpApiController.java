package io.nicheblog.dreamdiary.api.snmp.controller;

import io.nicheblog.dreamdiary.api.snmp.model.SnmpApiParam;
import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.SnmpUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * SnmpApiController
 * <pre>
 *  API:: Snmp 수동 송신 관련 Controller.
 * </pre>
 *
 * @author nichefish
 */
@RestController
@CrossOrigin(origins="*", allowedHeaders="*")   // CORS 에러 해결 위한 조치
@Log4j2
public class SnmpApiController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.ADMIN_PAGE;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.ADMIN;      // 작업 카테고리 (로그 적재용)

    /**
     * API:: SNMP 내역을 송신한다.
     * 비로그인 사용자도 외부에서 접근 가능. (인증 없음)
     * 
     * @param snmpApiParam - SNMP API 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param ipAddr - 송신할 IP 주소
     * @return {@link ResponseEntity} -- SNMP 전송 결과를 담은 ResponseEntity 객체
     */
    @PostMapping(Url.URL_API_SNMP_SEND_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> snmpSendAjax (
            final SnmpApiParam snmpApiParam,
            final LogActvtyParam logParam,
            final @RequestParam("ipAddr") String ipAddr
        ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            SnmpApiParam snmpSendInfo = new SnmpApiParam(ipAddr);
            snmpSendInfo.setIpAddr(ipAddr);
            SnmpUtils.sendSnmpMessage(snmpSendInfo);
            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            log.info("{} / isSuccess: {}, rsltMsg: {}", request.getRequestURI(), isSuccess, rsltMsg);
            // TODO: 로그 관련 처리
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

}