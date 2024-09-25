package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.model.admin.TmplatDefDto;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.service.admin.TmplatDefService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;

/**
 * TmplatTxtController
 * <pre>
 *  템플릿 관리 컨트롤러
 * </pre>
 * TODO: 신규개발 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class TmplatTxtController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.TMPLAT_DEF_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.TMPLAT;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "tmplatDefService")
    private TmplatDefService tmplatDefService;

    /**
     * 템플릿 관리 등록/수정 처리 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {Url.TMPLAT_TXT_REG_AJAX, Url.TMPLAT_TXT_MDF_AJAX})
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> tmplatTxtRegAjax(
            final @Valid TmplatDefDto tmplatDto,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 등록/수정 처리
            TmplatDefDto result = tmplatDefService.regist(tmplatDto);
            // isSuccess = (StringUtils.isNotEmpty(rsDto.getTmplatCd()));
            ajaxResponse.setRsltObj(result);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
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
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
}
