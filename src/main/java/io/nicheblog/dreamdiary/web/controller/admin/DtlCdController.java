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
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.service.admin.DtlCdService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * 상세 코드 관리 (useYn=N 포함) 상세 조회 (Ajax)
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
        String resultMsg = "";
        try {
            DtlCd dtlCdDto = dtlCdService.getDtlDto(cmmDtlKey);
            if (dtlCdDto != null) {
                isSuccess = true;
                ajaxResponse.setResultObj(dtlCdDto);
            }
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
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
     * 상세 코드 관리 (useYn=N 포함) 등록 (Ajax)
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
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = "Y".equals(regYn);
            DtlCd result = isReg ? dtlCdService.regist(dtlCd) : dtlCdService.modify(dtlCd, dtlCdKey);
            isSuccess = (result.getDtlCd() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(dtlCd.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
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
            final DtlCdKey cmmDtlKey
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = dtlCdService.setInUse(cmmDtlKey);
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
     * 상세 코드 관리(useYn=N 포함) 미사용 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.DTL_CD_UNUSE_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dtlCdUnuseAjax(
            final LogActvtyParam logParam,
            final @RequestParam("clCd") String clCd,
            final @RequestParam("dtlCd") String dtlCd
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            DtlCdKey key = new DtlCdKey(clCd, dtlCd);
            isSuccess = dtlCdService.setInUnuse(key);
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
     * 상세 코드 관리 (useYn=N 포함) 삭제
     * (관리자MNGR만 접근 가능)
     *
     * @param clCd: 구분코드 (대분류)
     * @param dtlCd: 상세코드
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
        String resultMsg = "";
        try {
            isSuccess = dtlCdService.delete(dtlCdKey);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
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
     * 분류 코드로 상세 코드 관리 (useYn=N 포함) 목록 조회 (Ajax)
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
        String resultMsg = "";
        try {
            Map<String, Object> searchParam = new HashMap<String, Object>();
            searchParam.put("clCd", clCd);

            Page<DtlCd> dtlCdPage = dtlCdService.getListDto(searchParam, Pageable.unpaged());
            List<DtlCd> dtlCdList = dtlCdPage.getContent();
            if (dtlCdList != null) {
                isSuccess = true;
                ajaxResponse.setResultList(dtlCdList);
            }
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
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

}
