package io.nicheblog.dreamdiary.web.controller.jrnl.day;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagSearchParam;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDto;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDaySearchParam;
import io.nicheblog.dreamdiary.web.service.jrnl.day.JrnlDayService;
import io.nicheblog.dreamdiary.web.service.jrnl.day.JrnlDayTagService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JrnlDayTagController
 * <pre>
 *  저널 일자 태그 Controller
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class JrnlDayTagController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.JRNL_DAY_PAGE;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.JRNL;        // 작업 카테고리 (로그 적재용)

    private final JrnlDayService jrnlDayService;
    private final JrnlDayTagService jrnlDayTagService;

    /**
     * 저널 일자 태그 전체 목록 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.JRNL_DAY_TAG_LIST_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlDayTagListAjax(
            @ModelAttribute("searchParam") TagSearchParam searchParam,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 전체 태그 목록 조회 (태그클라우드)
            List<TagDto> tagList = jrnlDayTagService.getDaySizedListDto(searchParam.getYy(), searchParam.getMnth());
            ajaxResponse.setRsltList(tagList);

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
     * 저널 일자 태그 전체 목록 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.JRNL_DAY_TAG_GROUP_LIST_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlDayTagGroupListAjax(
            @ModelAttribute("searchParam") TagSearchParam searchParam,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 전체 태그 목록 조회 (태그클라우드)
            Map<String, List<TagDto>> tagGroupMap = jrnlDayTagService.getDaySizedGroupListDto(searchParam.getYy(), searchParam.getMnth());
            ajaxResponse.setRsltMap(tagGroupMap);

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
     * 저널 일자 태그 상세 (해당 태그 꿈 목록) 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(value = {Url.JRNL_DAY_TAG_DTL_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlDayTagDtlAjax(
            JrnlDaySearchParam searchParam,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 목록 조회 및 응답에 추가
            List<JrnlDayDto> jrnlDayList = jrnlDayService.jrnlDayTagDtl(searchParam);
            Collections.sort(jrnlDayList);
            ajaxResponse.setRsltList(jrnlDayList);

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
}
