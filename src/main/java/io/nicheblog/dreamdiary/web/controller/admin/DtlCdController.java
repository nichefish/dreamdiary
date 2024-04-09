package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCd;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.admin.DtlCdParam;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.service.admin.DtlCdService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DtlCdController
 * <pre>
 *  상세코드(dtlCd) 정보 관리 컨트롤러
 *  ※상세코드(dtl_cd) = 분류코드 하위의 상세코드. 분류코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class DtlCdController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.CL_CD_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.CD;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "dtlCdService")
    private DtlCdService dtlCdService;

    /**
     * 상세 코드 관리(useYn=N 포함) 상세 조회 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.DTL_CD_DTL_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dtlCdDtlAjax(
            final LogActvtyParam logParam,
            final DtlCdKey cmmDtlKey
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            DtlCd dtlCdDto = dtlCdService.getDtlDto(cmmDtlKey);
            ajaxResponse.setRsltObj(dtlCdDto);

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
     * 상세 코드 관리(useYn=N 포함) 등록/수정 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {SiteUrl.DTL_CD_REG_AJAX, SiteUrl.DTL_CD_MDF_AJAX})
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dtlCdRegAjax(
            final @Valid DtlCd dtlCd,
            final DtlCdKey dtlCdKey,
            final LogActvtyParam logParam,
            final @RequestParam("regYn") String regYn,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // Validation
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            // 등록/수정 처리
            boolean isReg = "Y".equals(regYn);
            DtlCd result = isReg ? dtlCdService.regist(dtlCd) : dtlCdService.modify(dtlCd, dtlCdKey);
            
            isSuccess = (result.getDtlCd() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(dtlCd.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 상세 코드 관리(useYn=N 포함) 사용 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.DTL_CD_USE_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dtlCdUseAjax(
            final LogActvtyParam logParam,
            final DtlCdKey dtlKey
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상태 변경 처리
            isSuccess = dtlCdService.setStateUse(dtlKey);
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
     * 상세 코드 관리(useYn=N 포함) 미사용 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.DTL_CD_UNUSE_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dtlCdUnuseAjax(
            final LogActvtyParam logParam,
            final DtlCdKey dtlKey
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상태 변경 처리
            isSuccess = dtlCdService.setStateUnuse(dtlKey);
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
     * 상세 코드 관리(useYn=N 포함) 삭제
     * (관리자MNGR만 접근 가능)
     *
     * @param dtlCdKey: clCd, dtlCd
     */
    @PostMapping(SiteUrl.DTL_CD_DEL_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dtlCdDelAjax(
            final LogActvtyParam logParam,
            final DtlCdKey dtlCdKey
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제 처리
            isSuccess = dtlCdService.delete(dtlCdKey);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
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
     * 분류 코드로 상세 코드 관리(useYn=N 포함) 목록 조회 (Ajax)
     * (관리자MNGR만 접근 가능)
     *
     * @param clCd: 구분코드 (대분류)
     */
    @RequestMapping(SiteUrl.DTL_CD_LIST_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dtlCdListAjax(
            final LogActvtyParam logParam,
            final @RequestParam("clCd") String clCd
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 목록 조회
            Map<String, Object> searchParam = new HashMap<String, Object>();
            searchParam.put("clCd", clCd);
            List<DtlCd> dtlCdList = dtlCdService.getListDto(searchParam);
            ajaxResponse.setRsltList(dtlCdList);

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
     * 관리자 > 메뉴 관리 > 정렬 순서 저장 (드래그앤드랍 결과 반영) (Ajax)
     */
    @PostMapping(SiteUrl.DTL_CD_SORT_ORDR_AJAX)
    @ResponseBody
    public AjaxResponse dtlCdSortOrdrAjax(
            @RequestBody DtlCdParam dtlCdParam,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = null;
        try {
            // 메뉴 정렬 순서 저장
            isSuccess = dtlCdService.sortOrdr(dtlCdParam.getSortOrdr());
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // logParam.setCn("key: " + menuNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ajaxResponse;
    }
}
