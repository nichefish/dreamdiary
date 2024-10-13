package io.nicheblog.dreamdiary.domain.notice.controller;

import io.nicheblog.dreamdiary.adapter.jandi.model.JandiParam;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeDto;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeSearchParam;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeXlsxDto;
import io.nicheblog.dreamdiary.domain.notice.service.NoticeService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common._clsf.managt.event.ManagtrAddEvent;
import io.nicheblog.dreamdiary.global._common._clsf.tag.event.TagProcEvent;
import io.nicheblog.dreamdiary.global._common._clsf.tag.service.TagService;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.global._common.cd.service.DtlCdService;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global._common.xlsx.XlsxType;
import io.nicheblog.dreamdiary.global._common.xlsx.util.XlsxUtils;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

/**
 * NoticeController
 * <pre>
 *  공지사항 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class NoticeApiController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.NOTICE_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.NOTICE;      // 작업 카테고리 (로그 적재용)

    private final NoticeService noticeService;
    private final DtlCdService dtlCdService;
    private final TagService tagService;

    /**
     * 공지사항 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param notice 등록/수정 처리할 객체
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param jandiParam 잔디 파라미터 객체
     * @param request - Multipart 요청
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(value = {Url.NOTICE_REG_AJAX, Url.NOTICE_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeRegAjax(
            final @Valid NoticeDto.DTL notice,
            final @RequestParam("postNo") @Nullable Integer key,
            final LogActvtyParam logParam,
            final JandiParam jandiParam,
            final MultipartHttpServletRequest request
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 등록/수정 처리
            final boolean isReg = (key == null);
            final NoticeDto result = isReg ? noticeService.regist(notice, request) : noticeService.modify(notice, request);
            ajaxResponse.setRsltObj(result);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            if (isSuccess) {
                // 태그 처리 :: 메인 로직과 분리
                publisher.publishEvent(new TagProcEvent(this, result.getClsfKey(), notice.tag));
                // 조치자 추가 :: 메인 로직과 분리
                publisher.publishEvent(new ManagtrAddEvent(this, result.getClsfKey()));
                // 잔디 메세지 발송 :: 메인 로직과 분리
                // if ("Y".equals(jandiYn)) {
                //     String jandiRsltMsg = notifyService.notifyNoticeReg(trgetTopic, result, logParam);
                //     rsltMsg = rsltMsg + "\n" + jandiRsltMsg;
                // }
            }
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(notice.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 공지사항 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @GetMapping(Url.NOTICE_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeDtlAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            final NoticeDto rsDto = noticeService.getDtlDto(key);
            ajaxResponse.setRsltObj(rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            noticeService.hitCntUp(key);
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 공지사항 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.NOTICE_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticeDelAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
            isSuccess = noticeService.delete(key);
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            if (isSuccess) {
                // 태그 처리 :: 메인 로직과 분리
                publisher.publishEvent(new TagProcEvent(this, new BaseClsfKey(key, ContentType.NOTICE)));
            }
        } catch (Exception e) {
            logParam.setExceptionInfo(e);

            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 공지사항 팝업공지 목록 조회 (Ajax)
     * 비로그인 사용자도 외부에서 접근 가능 (인증 없음)
     */
    @GetMapping(Url.NOTICE_POPUP_LIST_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> noticePopupListAjax(
            final NoticeSearchParam searchParam,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 팝업공지 목록 조회
            searchParam.setPopupYn("Y");
            searchParam.setManagtStartDt(DateUtils.getCurrDateAddDay(-7));
            final Sort sort = Sort.by(Sort.Direction.ASC, "managt.managtDt");
            final List<NoticeDto.LIST> noticeList = noticeService.getListDto(searchParam, sort);
            ajaxResponse.setRsltList(noticeList);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 반복적으로 호출되므로 실패(Exception)시 외에는 로그 적재하지 않음
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 휴가 관리 > 휴가사용일자 > 휴가사용일자 엑셀 다운로드
     * (관리자MNGR만 접근 가능.)
     */
    @GetMapping(Url.NOTICE_LIST_XLSX_DOWNLOAD)
    @Secured(Constant.ROLE_MNGR)
    public void vcatnSchdulXlsxDownload(
            final NoticeSearchParam searchParam,
            final LogActvtyParam logParam
    ) throws Exception {

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 접근방식 1. stream 객체 전달
            final Stream<NoticeXlsxDto> xlsxStream = noticeService.getStreamXlsxDto(searchParam);
            XlsxUtils.listXlxsDownload(XlsxType.NOTICE, xlsxStream);
            // 접근방식 2. ???

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.VCATN_SCHDUL_LIST);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }

}