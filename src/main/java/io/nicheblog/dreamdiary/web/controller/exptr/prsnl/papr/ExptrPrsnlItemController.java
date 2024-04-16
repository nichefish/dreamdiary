package io.nicheblog.dreamdiary.web.controller.exptr.prsnl.papr;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.file.utils.FileUtils;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.web.service.exptr.prsnl.papr.ExptrPrsnlItemService;
import io.nicheblog.dreamdiary.web.service.exptr.prsnl.papr.ExptrPrsnlPaprService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;

/**
 * ExptrPrsnlItemController
 * <pre>
 *  경비 관리 > 경비지출항목 관리 컨트롤러
 *  ※경비지출항목(exptr_prsnl_item) = 경비지출서(exptr_prsnl_papr)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class ExptrPrsnlItemController
        extends BaseControllerImpl {

    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.EXPTR_PRSNL_PAPR;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "exptrPrsnlPaprService")
    private ExptrPrsnlPaprService exptrPrsnlPaprService;
    @Resource(name = "exptrPrsnlItemService")
    private ExptrPrsnlItemService exptrPrsnlItemService;

    /**
     * 경비 관리 > 경비지출서 > 경비지출내역 목록 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.EXPTR_PRSNL_ITEM_LIST_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrPrsnlItemListAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 항목 조회 및 응답에 추가
            ExptrPrsnlPaprDto rsDto = exptrPrsnlPaprService.getDtlDto(key);
            ajaxResponse.setRsltList(rsDto.getItemList());

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
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }


    /**
     * 경비 관리 > 경비지출서 > 경비지출서 개별항목 영수증 업로드 및 업데이트
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = Url.EXPTR_PRSNL_ITEM_RCIPT_UPLOAD_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrPrsnlItemRciptUploadAjax(
            final LogActvtyParam logParam,
            final @RequestParam("exptrPrsnlItemNo") Integer exptrPrsnlItemNo,
            final MultipartHttpServletRequest request
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 파일 영역 처리 후, 성공시 업로드 정보 받아서 반환
            AtchFileDtlDto atchfileDtl = FileUtils.uploadDtlFile(request);
            Integer atchFileDtlNo = atchfileDtl.getAtchFileDtlNo();

            isSuccess = (atchFileDtlNo != null);
            if (isSuccess) {
                isSuccess = exptrPrsnlItemService.updateRciptFile(exptrPrsnlItemNo, atchFileDtlNo);
            }
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + exptrPrsnlItemNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 경비지출누적집계 > 경비지출서 해당 지출내역에 대하여 영수증 원본 제출여부 업데이트
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.EXPTR_PRSNL_ITEM_ORGNL_RCIPT_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrPrsnlItemOrgnlRcipt(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final @RequestParam("exptrPrsnlItemNo") Integer exptrPrsnlItemNo,
            final @RequestParam("orgnlRciptYn") String orgnlRciptYn
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상태 변경 처리
            isSuccess = exptrPrsnlItemService.updtOrgnlRciptYn(key, exptrPrsnlItemNo, orgnlRciptYn);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + exptrPrsnlItemNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 경비 관리 > 경비지출누적집계 > 경비지출서 해당 지출내역 반려 처리 (관리자)
     * 관리자MNGR만 접근 가능
     */
    @PostMapping(Url.EXPTR_PRSNL_ITEM_RJECT_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> exptrPrsnlItemRject(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key,
            final @RequestParam("exptrPrsnlItemNo") Integer exptrPrsnlItemNo,
            final @RequestParam("rjectYn") String rjectYn,
            final @RequestParam("rjectResn") String rjectResn
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상태 변경 처리
            isSuccess = exptrPrsnlItemService.exptrPrsnlItemRject(key, exptrPrsnlItemNo, rjectYn, rjectResn);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + exptrPrsnlItemNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

}