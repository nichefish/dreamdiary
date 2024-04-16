package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.snmp.SnmpParam;
import io.nicheblog.dreamdiary.global.util.snmp.SnmpUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SnmpController
 * <pre>
 *  Snmp 수동 송신 관련 Controller
 * </pre>
 *
 * @author nichefish
 */
@Controller
@CrossOrigin(origins="*", allowedHeaders="*")   // CORS 에러 해결 위한 조치
@Log4j2
public class SnmpController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.ADMIN_PAGE;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.ADMIN;      // 작업 카테고리 (로그 적재용)

    /**
     * API:: SNMP 내역 송신
     * 비로그인 사용자도 외부에서 접근 가능 (인증 없음)
     */
    @RequestMapping(Url.URL_SNMP_SEND_AJAX)
    @ResponseBody
    public AjaxResponse snmpSendAjax (
            final SnmpParam snmpParam,
            final LogActvtyParam logParam,
            final @RequestParam("ipAddr") String ipAddr
        ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            SnmpParam snmpSendInfo = new SnmpParam(ipAddr);
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
        
        return ajaxResponse;
    }

}