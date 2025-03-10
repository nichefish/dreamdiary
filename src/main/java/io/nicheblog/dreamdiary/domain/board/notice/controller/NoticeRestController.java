package io.nicheblog.dreamdiary.domain.board.notice.controller;

import io.nicheblog.dreamdiary.domain.board.notice.model.NoticeDto;
import io.nicheblog.dreamdiary.domain.board.notice.model.NoticeSearchParam;
import io.nicheblog.dreamdiary.domain.board.notice.service.NoticeService;
import io.nicheblog.dreamdiary.extension.clsf.tag.handler.TagProcEventListener;
import io.nicheblog.dreamdiary.extension.clsf.viewer.handler.ViewerEventListener;
import io.nicheblog.dreamdiary.extension.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.extension.log.actvty.aspect.LogActvtyRestControllerAspect;
import io.nicheblog.dreamdiary.extension.log.actvty.handler.LogActvtyEventListener;
import io.nicheblog.dreamdiary.extension.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.util.List;

/**
 * NoticeRestController
 * <pre>
 *  공지사항 API 컨트롤러.
 * </pre>
 *
 * @author nichefish
 * @see LogActvtyRestControllerAspect
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class NoticeRestController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.NOTICE_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.NOTICE;      // 작업 카테고리 (로그 적재용)

    private final NoticeService noticeService;

    /**
     * 공지사항 팝업공지 목록 조회 (Ajax)
     * 비로그인 사용자도 외부에서 접근 가능 (인증 없음)
     *
     * @param searchParam 검색 조건 파라미터
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.NOTICE_POPUP_LIST_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticePopupListAjax(
            final NoticeSearchParam searchParam,
            final LogActvtyParam logParam
    ) throws Exception {

        // 팝업공지 목록 조회
        searchParam.setPopupYn("Y");
        searchParam.setManagtStartDt(DateUtils.getCurrDateAddDay(-7));
        final Sort sort = Sort.by(Sort.Direction.ASC, "managt.managtDt");
        final List<NoticeDto.LIST> noticeList = noticeService.getListDto(searchParam, sort);

        final boolean isSuccess = true;
        final String rsltMsg = MessageUtils.RSLT_SUCCESS;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg);

        // TODO: 반복적으로 호출되므로 실패(Exception)시 외에는 로그 적재하지 않아야 함
        // Aspect에서 예외 처리? 규칙에 적용되지 않도록 별도 컨트롤러 생성?

        return ResponseEntity.ok(AjaxResponse.withAjaxResult(isSuccess, rsltMsg).withList(noticeList));
    }

    /**
     * 공지사항 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param notice 등록/수정 처리할 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param request - Multipart 요청
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     * @see TagProcEventListener ,ManagtrEventListener
     */
    @PostMapping(value = {Url.NOTICE_REG_AJAX, Url.NOTICE_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeRegAjax(
            final @Valid NoticeDto.DTL notice,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request
    ) throws Exception {

        final boolean isReg = (notice.getKey() == null);
        final ServiceResponse result = isReg ? noticeService.regist(notice, request) : noticeService.modify(notice, request);
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg);

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }

    /**
     * 공지사항 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     * @see ViewerEventListener
     */
    @GetMapping(Url.NOTICE_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeDtlAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) throws Exception {

        final NoticeDto retrievedDto = noticeService.viewDtlPage(key);
        final boolean isSuccess = true;
        final String rsltMsg = MessageUtils.RSLT_SUCCESS;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg);

        return ResponseEntity.ok(AjaxResponse.withAjaxResult(isSuccess, rsltMsg).withObj(retrievedDto));
    }

    /**
     * 공지사항 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param postNo 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     * @see TagProcEventListener
     */
    @PostMapping(Url.NOTICE_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeDelAjax(
            final @RequestParam("postNo") Integer postNo,
            final LogActvtyParam logParam
    ) throws Exception {

        final ServiceResponse result = noticeService.delete(postNo);
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = MessageUtils.RSLT_SUCCESS;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg);

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }

    /**
     * 공지사항 엑셀 다운로드
     * (관리자MNGR만 접근 가능.)
     *
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * TODO: AOP 예외 처리
     * @see LogActvtyEventListener
     */
    @GetMapping(Url.NOTICE_LIST_XLSX_DOWNLOAD)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeListXlsxDownload(
            final NoticeSearchParam searchParam,
            final LogActvtyParam logParam
    ) throws Exception {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
/*        try {
            // 접근방식 1. stream 객체 전달
            final Stream<NoticeXlsxDto> xlsxStream = noticeService.getStreamXlsxDto(searchParam);
            XlsxUtils.listXlxsDownload(XlsxType.NOTICE, xlsxStream);
            // 접근방식 2. ???

            isSuccess = true;
            rsltMsg = MessageUtils.RSLT_SUCCESS;
        } catch (final Exception e) {
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.VCATN_SCHDUL_LIST);
        } finally {
            // 로그 관련 세팅
            logParam.setResult(isSuccess, rsltMsg);
            publisher.publishAsyncEvent(new LogActvtyEvent(this, logParam));
        }*/

        return ResponseEntity.ok(ajaxResponse);
    }

}