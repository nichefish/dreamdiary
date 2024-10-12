package io.nicheblog.dreamdiary.domain.jrnl.sumry.controller;

import io.nicheblog.dreamdiary.domain._clsf.tag.event.TagProcEvent;
import io.nicheblog.dreamdiary.domain._clsf.tag.model.TagDto;
import io.nicheblog.dreamdiary.domain._core.cd.service.DtlCdService;
import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.domain.jrnl.day.service.JrnlDayTagService;
import io.nicheblog.dreamdiary.domain.jrnl.diary.service.JrnlDiaryService;
import io.nicheblog.dreamdiary.domain.jrnl.diary.service.JrnlDiaryTagService;
import io.nicheblog.dreamdiary.domain.jrnl.dream.service.JrnlDreamService;
import io.nicheblog.dreamdiary.domain.jrnl.dream.service.JrnlDreamTagService;
import io.nicheblog.dreamdiary.domain.jrnl.sumry.model.JrnlSumryDto;
import io.nicheblog.dreamdiary.domain.jrnl.sumry.model.JrnlSumrySearchParam;
import io.nicheblog.dreamdiary.domain.jrnl.sumry.service.JrnlSumryService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.SiteMenu;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.List;

/**
 * JrnlSumryController
 * <pre>
 *  저널 결산 Controller
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
public class JrnlSumryController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.JRNL_SUMRY_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.JRNL;        // 작업 카테고리 (로그 적재용)

    private final JrnlSumryService jrnlSumryService;
    private final JrnlDiaryService jrnlDiaryService;
    private final JrnlDreamService jrnlDreamService;
    private final JrnlDayTagService jrnlDayTagService;
    private final JrnlDiaryTagService jrnlDiaryTagService;
    private final JrnlDreamTagService jrnlDreamTagService;
    private final DtlCdService dtlCdService;

    /**
     * 저널 결산 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
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
            final JrnlSumryDto totalSumry = jrnlSumryService.getTotalSumry();
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
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @GetMapping(value = {Url.JRNL_SUMRY_LIST_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryListAjax(
            JrnlSumrySearchParam searchParam,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 목록 조회 및 응답에 추가
            final List<JrnlSumryDto.LIST> jrnlSumryList = jrnlSumryService.getListDto(searchParam);
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 저널 결산 상세 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(value = Url.JRNL_SUMRY_DTL)
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
            final JrnlSumryDto rsltDto = key != null ? jrnlSumryService.getSumryDtl(key) : yyParam != null ? jrnlSumryService.getDtlDtoByYy(yyParam) : null;
            model.addAttribute("post", rsltDto);
            if (rsltDto != null) {
                final Integer yy = rsltDto.getYy();
                // 중요 일기 목록 조회
                model.addAttribute("imprtcDiaryList", jrnlDiaryService.getImprtcDiaryList(yy));
                // 중요 꿈 목록 조회
                model.addAttribute("imprtcDreamList", jrnlDreamService.getImprtcDreamList(yy));

                // 일자 태그 목록 조회
                final List<TagDto> jrnlDayTagList = jrnlDayTagService.getDaySizedListDto(yy, 99);
                model.addAttribute("dayTagList", jrnlDayTagList);
                // 일기 태그 목록 조회
                final List<TagDto> jrnlDiaryTagList = jrnlDiaryTagService.getDiarySizedListDto(yy, 99);
                model.addAttribute("diaryTagList", jrnlDiaryTagList);
                // 꿈 태그 목록 조회
                final List<TagDto> jrnlDreamTagList = jrnlDreamTagService.getDreamSizedListDto(yy, 99);
                model.addAttribute("dreamTagList", jrnlDreamTagList);
            }
            // 코드 데이터 모델에 추가
            dtlCdService.setCdListToModel(Constant.JRNL_SUMRY_TY_CD, model);

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
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @GetMapping(value = {Url.JRNL_SUMRY_DTL_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryDtlAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            final JrnlSumryDto.DTL rslt = jrnlSumryService.getSumryDtl(key);
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 특정 년도에 대해 저널 결산 생성 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param yy 년도
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(value = {Url.JRNL_SUMRY_MAKE_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryMakeAjax(
            final @RequestParam("yy") Integer yy,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 전체 년도에 대해 저널 결산 생성 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(value = {Url.JRNL_SUMRY_MAKE_TOTAL_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryMakeTotalAjax(
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 저널 결산 꿈 기록 완료 처리 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     */
    @PostMapping(value = {Url.JRNL_SUMRY_DREAM_COMPT_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryDreamComptAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
            isSuccess = jrnlSumryService.dreamCompt(key);
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 저널 결산 내용 수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param jrnlSumry 등록/수정 처리할 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(value = {Url.JRNL_SUMRY_REG_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSumryTagAjax(
            final @Valid JrnlSumryDto.DTL jrnlSumry,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            final JrnlSumryDto result = jrnlSumryService.modify(jrnlSumry, request);
            ajaxResponse.setRsltObj(result);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 태그 처리
            publisher.publishEvent(new TagProcEvent(this, jrnlSumry.getClsfKey(), jrnlSumry.tag));
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
