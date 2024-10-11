package io.nicheblog.dreamdiary.domain.jrnl;

import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.domain._clsf.tag.model.TagSearchParam;
import io.nicheblog.dreamdiary.domain.jrnl.day.service.JrnlDayTagCtgrSynchronizer;
import io.nicheblog.dreamdiary.domain.jrnl.diary.service.JrnlDiaryTagCtgrSynchronizer;
import io.nicheblog.dreamdiary.domain.jrnl.dream.service.JrnlDreamTagCtgrSynchronizer;
import io.nicheblog.dreamdiary.global.Constant;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * JrnlTagController
 * <pre>
 *  저널 태그 Controller.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
public class JrnlTagController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.JRNL_DAY_PAGE;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.JRNL;        // 작업 카테고리 (로그 적재용)

    private final JrnlDreamTagCtgrSynchronizer jrnlDreamTagCtgrSynchronizer;
    private final JrnlDiaryTagCtgrSynchronizer jrnlDiaryTagCtgrSynchronizer;
    private final JrnlDayTagCtgrSynchronizer jrnlDayTagCtgrSynchronizer;

    /**
     * 저널 태그 카테고리 메타 파일 - DB 동기화 (Ajax)
     * (관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.JRNL_TAG_CTGR_SYNC_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> tagCtgrSyncAjax(
            @ModelAttribute("searchParam") TagSearchParam searchParam,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 저널 일자 태그 동기화
            jrnlDayTagCtgrSynchronizer.tagSync();
            // 저널 일기 태그 동기화
            jrnlDiaryTagCtgrSynchronizer.tagSync();
            // 저널 꿈 태그 동기화
            jrnlDreamTagCtgrSynchronizer.tagSync();

            // 브라우저 캐시 초기화 처리
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

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
