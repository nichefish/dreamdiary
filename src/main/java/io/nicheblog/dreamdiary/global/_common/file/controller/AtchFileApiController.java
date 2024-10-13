package io.nicheblog.dreamdiary.global._common.file.controller;

import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global._common.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global._common.file.service.AtchFileDtlService;
import io.nicheblog.dreamdiary.global._common.file.utils.FileUtils;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

/**
 * AtchFileApiController
 * <pre>
 *  파일 처리 API 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class AtchFileApiController
        extends BaseControllerImpl {

    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.FILE;      // 작업 카테고리 (로그 적재용)

    private final AtchFileDtlService atchFileDtlService;

    /**
     * 파일 유무 여부 체크 (Ajax) - 첨부파일 상세 ID(atchFileDtlNo) 이용.
     * (로그인 사용자만 접근 가능.)
     *
     * @param fileId 파일 ID. 체크할 파일의 고유 식별자
     * @param logParam 로그 관련 파라미터. 처리 과정에서 필요한 로그 정보를 포함
     * @return ResponseEntity -- 응답 객체
     */
    @GetMapping(Url.FILE_DOWNLOAD_CHK_AJAX)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<AjaxResponse> fileChckAjax(
            final @RequestParam("fileId") @Nullable String fileId,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 체크 로직 처리
            isSuccess = FileUtils.fileChck(fileId);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            // 반복적으로 호출되므로 실패시 외에는 로그 적재하지 않음
            logParam.setCn("key: " + fileId);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 파일 목록 정보 조회 (Ajax) - 첨부파일 묶음 ID 이용. (atchFileNo)
     * 비로그인 사용자도 외부에서 접근 가능. (인증 없음)
     *
     * @param atchFileNo - 파일 번호. 조회할 파일의 고유 식별자
     * @param logParam 로그 관련 파라미터. 처리 과정에서 필요한 로그 정보를 포함
     * @return ResponseEntity -- 응답 객체
     */
    @GetMapping(Url.FILE_INFO_LIST_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> getFileList(
            final @RequestParam("atchFileNo") @Nullable Integer atchFileNo,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 목록 조회 및 응답에 세팅
            List<AtchFileDtlDto> fileList = atchFileDtlService.getPageDto(atchFileNo);
            ajaxResponse.setRsltList(fileList);

            isSuccess = (fileList != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + atchFileNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 파일 다운로드 : 첨부파일 상세 ID 이용 (atchFileDtlNo)
     * Ajax로 유무 체크 후 다운로드하므로 항상 파일이 존재한다 가정하고 진행
     * (로그인 사용자만 접근 가능.)
     *
     * @param atchFileDtlNo 파일 상세 번호. 다운로드할 파일의 고유 식별자
     * @param logParam 로그 관련 파라미터. 처리 과정에서 필요한 로그 정보를 포함
     * @throws Exception 다운로드 과정에서 발생할 수 있는 예외
     */
    @GetMapping(Url.FILE_DOWNLOAD)
    @PreAuthorize("isAuthenticated()")
    public void fileDownload(
            final @RequestParam("atchFileDtlNo") @Nullable Integer atchFileDtlNo,
            final LogActvtyParam logParam
    ) throws Exception {

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 파일 정보 조회
            AtchFileDtlEntity atchFileDtl = atchFileDtlService.getDtlEntity(atchFileDtlNo);
            String orgnFileNm = atchFileDtl.getOrgnFileNm();
            // 파일 다운로드 처리
            File file = new File(atchFileDtl.getFileStrePath(), atchFileDtl.getStreFileNm());
            FileUtils.downloadFile(file, orgnFileNm);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + atchFileDtlNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }

    /**
     * VOD 파일 다운로드 : 폴더명과 파일명 파라미터로 넘김
     * Ajax로 유무 체크 후 다운로드하므로 항상 파일이 존재한다 가정하고 진행
     * (로그인 사용자만 접근 가능.)
     *
     * @param dirName 다운로드할 파일이 위치한 폴더명
     * @param fileName 다운로드할 파일명
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @throws Exception 다운로드 중 발생할 수 있는 예외
     */
    @GetMapping("/file/vod/{dir}/{fileName}")
    @PreAuthorize("isAuthenticated()")
    public void staticVodFileDownload(
            final @PathVariable String dirName,
            final @PathVariable String fileName,
            final LogActvtyParam logParam
    ) throws Exception {

        boolean isSuccess = false;
        String rsltMsg = "";
        String dir = "";
        try {
            if ("ceremony".equals(dirName)) dir = "시무식";
            File file = new File("vod-storage/" + dir + "/" + fileName);
            log.debug("file: {}", file);
            FileUtils.downloadFile(file, fileName);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + dirName + "/" + fileName);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }

    /**
     * 정적 파일 다운로드 : /content 폴더 아래에 위치한 파일명으로 다운로드
     * Ajax로 유무 체크 후 다운로드하므로 항상 파일이 존재한다 가정하고 진행
     * (로그인 사용자만 접근 가능.)
     *
     * @param fileName 다운로드할 파일명
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @throws Exception 다운로드 중 발생할 수 있는 예외
     */
    @GetMapping("/file/{fileName}")
    public void staticFileDownload(
            final @PathVariable String fileName,
            final LogActvtyParam logParam
    ) throws Exception {

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            File file = new File("content/" + fileName);
            FileUtils.downloadFile(file, fileName);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + fileName);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }

    /**
     * 파일 업로드 : 업로드 후 AtchDtlFileDto 반환 (filepath 정보 포함)
     * (로그인 사용자만 접근 가능.)
     *
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param request Multipar 요청
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 파일 업로드 중 발생할 수 있는 예외
     */
    @PostMapping(Url.FILE_UPLOAD_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> uploadFileAjax(
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 파일 영역 처리 후 업로드 정보 받아서 반환
            AtchFileDtlDto atchfileDtl = FileUtils.uploadDtlFile(request);
            ajaxResponse.setRsltObj(atchfileDtl);

            assert atchfileDtl != null;
            isSuccess = (atchfileDtl.getAtchFileDtlNo() != null);
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
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
}
