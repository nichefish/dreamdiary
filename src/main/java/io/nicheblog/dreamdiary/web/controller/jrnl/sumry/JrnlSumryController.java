package io.nicheblog.dreamdiary.web.controller.jrnl.sumry;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.model.jrnl.sumry.JrnlSumryDto;
import io.nicheblog.dreamdiary.web.model.jrnl.sumry.JrnlSumrySearchParam;
import io.nicheblog.dreamdiary.web.service.jrnl.diary.JrnlDiaryService;
import io.nicheblog.dreamdiary.web.service.jrnl.dream.JrnlDreamService;
import io.nicheblog.dreamdiary.web.service.jrnl.dream.JrnlDreamTagService;
import io.nicheblog.dreamdiary.web.service.jrnl.sumry.JrnlSumryService;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JrnlSumryController
 * <pre>
 *  저널 결산 Controller
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
public class JrnlSumryController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.JRNL_SUMRY_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.JRNL;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "jrnlSumryService")
    private JrnlSumryService jrnlSumryService;
    @Resource(name = "jrnlDiaryService")
    private JrnlDiaryService jrnlDiaryService;
    @Resource(name = "jrnlDreamService")
    private JrnlDreamService jrnlDreamService;
    @Resource(name = "jrnlDreamTagService")
    private JrnlDreamTagService jrnlDreamTagService;
    @Resource(name = "cdService")
    private CdService cdService;

    /**
     * 저널 결산 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.JRNL_SUMRY_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String jrnlSumryPage(
            @ModelAttribute("searchParam") JrnlSumrySearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.JRNL_SUMRY.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 전체 통계 조회
            JrnlSumryDto totalSumry = jrnlSumryService.getTotalSumry();
            model.addAttribute("totalSumry", totalSumry);
            // 목록 조회 및 모델에 추가
            List<JrnlSumryDto.LIST> jrnlSumryList = jrnlSumryService.getListDto(searchParam);
            model.addAttribute("jrnlSumryList", jrnlSumryList);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.ADMIN_MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/jrnl/sumry/jrnl_sumry_list";
    }

    /**
     * 저널 결산 목록 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(value = {Url.JRNL_SUMRY_LIST_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryListAjax(
            JrnlSumrySearchParam searchParam,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 목록 조회 및 응답에 추가
            List<JrnlSumryDto.LIST> jrnlSumryList = jrnlSumryService.getListDto(searchParam);
            ajaxResponse.setRsltList(jrnlSumryList);

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
     * 저널 결산 상세 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(value = Url.JRNL_SUMRY_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String jrnlSumryDtl(
            final @RequestParam("postNo") @Nullable Integer key,
            final @RequestParam("yy") @Nullable Integer yyParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.JRNL_SUMRY.setAcsPageInfo(Constant.PAGE_DTL));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 모델에 추가
            JrnlSumryDto rsltDto = key != null ? jrnlSumryService.getSumryDtl(key) : yyParam != null ? jrnlSumryService.getDtlDtoByYy(yyParam) : null;
            model.addAttribute("post", rsltDto);
            if (rsltDto != null) {
                Integer yy = rsltDto.getYy();
                // 중요 일기 목록 조회
                model.addAttribute("imprtcDiaryList", jrnlDiaryService.getImprtcDiaryList(yy));
                // 중요 꿈 목록 조회
                model.addAttribute("imprtcDreamList", jrnlDreamService.getImprtcDreamList(yy));

                // 꿈 태그 목록 조회
                Map<String, Object> searchParamMap = new HashMap<>() {{
                    put("contentType", ContentType.JRNL_DREAM.key);
                    put("yy", yy);
                }};
                List<TagDto> jrnlDreamTagList = jrnlDreamTagService.getDreamSizedListDto(searchParamMap);
                model.addAttribute("dreamTagList", jrnlDreamTagList);
            }
            // 코드 데이터 모델에 추가
            cdService.setModelCdData(Constant.JRNL_SUMRY_TY_CD, model);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + (key != null ? key.toString() : yyParam));
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/jrnl/sumry/jrnl_sumry_dtl";
    }
    
    /**
     * 저널 결산 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(value = {Url.JRNL_SUMRY_DTL_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryDtlAjax(
            JrnlSumrySearchParam searchParam,
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            JrnlSumryDto.DTL rslt = jrnlSumryService.getDtlDto(key);
            ajaxResponse.setRsltObj(rslt);

            isSuccess = (rslt.getPostNo() != null);
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
     * 저널 결산 생성 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {Url.JRNL_SUMRY_MAKE_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryMakeAjax(
            final @RequestParam("yy") Integer yy,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제 처리
            isSuccess = jrnlSumryService.makeYySumry(yy);
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
     * 저널 결산 생성 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {Url.JRNL_SUMRY_MAKE_TOTAL_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryMakeTotalAjax(
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제 처리
            isSuccess = jrnlSumryService.makeTotalYySumry();
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
}
