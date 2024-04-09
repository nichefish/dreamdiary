package io.nicheblog.dreamdiary.web.controller.cmm.flsys;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.flsys.FlsysMetaDto;
import io.nicheblog.dreamdiary.web.service.cmm.flsys.FlsysMetaService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;

/**
 * FlsysMetaController
 * <pre>
 *  파일시스템 메타 관리 컨트롤러
 * </pre>
 * TODO: 보완 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class FlsysMetaController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.FLSYS_HOME;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.FLSYS;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "flsysMetaService")
    public FlsysMetaService flsysMetaService;

    /**
     * 파일시스템 메타 정보 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {SiteUrl.FLSYS_META_REG_AJAX, SiteUrl.FLSYS_META_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> flsysMetaRegAjax(
            final @Valid FlsysMetaDto flsysMeta,
            final BaseClsfKey key,
            final LogActvtyParam logParam,
            final BaseParam param,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = key.getPostNo() == null;
            FlsysMetaDto result = isReg ? flsysMetaService.regist(flsysMeta) : flsysMetaService.modify(flsysMeta, key);
            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(flsysMeta.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 파일시스템 메타 정보 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.FLSYS_META_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> flsysMetaDtlAjax(
            final LogActvtyParam logParam,
            final BaseClsfKey postKey
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 정보 조회 및 응답에 세팅
            postKey.setContentType("flsysMeta");
            FlsysMetaDto rsDto = flsysMetaService.getDtlDto(postKey);
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
            logParam.setCn("key: " + postKey.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 파일시스템 메타 정보 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.FLSYS_META_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> flsysMetaDelAjax(
            final LogActvtyParam logParam,
            final BaseClsfKey clsfKey
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 게시판 정보 조회
            clsfKey.setContentType("flsysMeta");
            isSuccess = flsysMetaService.delete(clsfKey);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + clsfKey.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
