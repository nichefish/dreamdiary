package io.nicheblog.dreamdiary.global.cmm.file.controller;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.file.service.AtchFileDtlService;
import io.nicheblog.dreamdiary.global.cmm.file.utils.FileUtils;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * AtchFileController
 * <pre>
 *  파일 처리 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class AtchFileController
        extends BaseControllerImpl {

    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.FILE;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "atchFileDtlService")
    private AtchFileDtlService atchFileDtlService;

    /**
     * 파일 유무 여부 체크 (Ajax)
     * 파일ID 이용 (atchFileDtlNo)
     * 로그인 사용자만 접근 가능
     */
    @RequestMapping(SiteUrl.FILE_DOWNLOAD_CHK_AJAX)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<AjaxResponse> fileChckAjax(
            final LogActvtyParam logParam,
            final @RequestParam("fileId") @Nullable String fileId
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = FileUtils.fileChck(fileId);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            logParam.setCn("key: " + fileId);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 반복적으로 호출되므로 실패시 외에는 로그 적재하지 않음
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 파일 목록 정보 조회 (Ajax)
     * 파일ID 이용 (atchFileNo)
     * 비로그인 사용자도 외부에서 접근 가능 (인증 없음)
     */
    @RequestMapping(SiteUrl.FILE_INFO_LIST_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> getFileList(
            final LogActvtyParam logParam,
            final @RequestParam("atchFileNo") @Nullable Integer atchFileNo
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            List<AtchFileDtlDto> fileList = atchFileDtlService.getPageDto(atchFileNo);
            isSuccess = (fileList != null);
            ajaxResponse.setResultList(fileList);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (NumberFormatException e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(new InvalidParameterException("파라미터 형식이 맞지 않습니다."));
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + atchFileNo);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 파일 다운로드 : 파일ID 이용 (atchFileDtlNo)
     * Ajax로 유무 체크 후 다운로드하므로 항상 파일이 존재한다 가정하고 진행
     * 로그인 사용자만 접근 가능
     */
    @RequestMapping(SiteUrl.FILE_DOWNLOAD)
    @PreAuthorize("isAuthenticated()")
    public void fileDownload(
            final LogActvtyParam logParam,
            final @RequestParam("atchFileDtlNo") @Nullable String atchFileDtlNoStr
    ) throws Exception {

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 파일 정보 조회
            assert atchFileDtlNoStr != null;    // dtlId = null일 경우 catch로 바로 넘김
            Integer atchFileDtlNo = Integer.parseInt(atchFileDtlNoStr);
            AtchFileDtlEntity atchFileDtl = atchFileDtlService.getDtlEntity(atchFileDtlNo);
            String orgnFileNm = atchFileDtl.getOrgnFileNm();
            // 파일 다운로드 처리 시작
            File file = new File(atchFileDtl.getFileStrePath(), atchFileDtl.getStreFileNm());
            FileUtils.downloadFile(file, orgnFileNm);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + atchFileDtlNoStr);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }

    /**
     * VOD 파일 다운로드 : 폴더명과 파일명 파라미터로 넘김
     * Ajax로 유무 체크 후 다운로드하므로 항상 파일이 존재한다 가정하고 진행
     * 로그인 사용자만 접근 가능
     */
    @RequestMapping("/file/vod/{dir}/{fileName}")
    @PreAuthorize("isAuthenticated()")
    public void staticVodFileDownload(
            final @PathVariable String dirName,
            final @PathVariable String fileName,
            final LogActvtyParam logParam
    ) throws Exception {

        boolean isSuccess = false;
        String resultMsg = "";
        String dir = "";
        try {
            if ("ceremony".equals(dirName)) dir = "시무식";
            File file = new File("vod-storage/" + dir + "/" + fileName);
            log.debug("file: {}", file);
            FileUtils.downloadFile(file, fileName);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + dirName + "/" + fileName);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }

    /**
     * 정적 파일 다운로드 : /content 폴더 아래에 위치한 파일명으로 다운로드
     * Ajax로 유무 체크 후 다운로드하므로 항상 파일이 존재한다 가정하고 진행
     * 로그인 사용자만 접근 가능
     */
    @RequestMapping("/file/{fileName}")
    public void staticFileDownload(
            final @PathVariable String fileName,
            final LogActvtyParam logParam
    ) throws Exception {

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            File file = new File("content/" + fileName);
            log.debug("file: {}", file);
            FileUtils.downloadFile(file, fileName);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + fileName);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }

    /**
     * 파일 업로드 : 업로드 후 AtchDtlFileDto 반환 (filepath 정보 포함)
     * 로그인 사용자만 접근 가능
     */
    @RequestMapping(SiteUrl.FILE_UPLOAD_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> uploadFileAjax(
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 파일 영역 처리 후 업로드 정보 받아서 반환
            AtchFileDtlDto atchfileDtl = FileUtils.uploadDtlFile(request);
            isSuccess = (atchfileDtl.getAtchFileDtlNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            if (isSuccess) ajaxResponse.setResultObj(atchfileDtl);
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
