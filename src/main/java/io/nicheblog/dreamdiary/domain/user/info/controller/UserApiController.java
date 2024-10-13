 package io.nicheblog.dreamdiary.domain.user.info.controller;

 import io.nicheblog.dreamdiary.domain.user.info.model.UserDto;
 import io.nicheblog.dreamdiary.domain.user.info.model.UserSearchParam;
 import io.nicheblog.dreamdiary.domain.user.info.service.UserService;
 import io.nicheblog.dreamdiary.global.Constant;
 import io.nicheblog.dreamdiary.global.Url;
 import io.nicheblog.dreamdiary.global._common.auth.service.AuthRoleService;
 import io.nicheblog.dreamdiary.global._common.auth.util.AuthUtils;
 import io.nicheblog.dreamdiary.global._common.cd.service.DtlCdService;
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
 import org.springframework.security.access.annotation.Secured;
 import org.springframework.web.bind.annotation.*;
 import org.springframework.web.multipart.MultipartHttpServletRequest;

 import javax.annotation.Nullable;
 import javax.validation.Valid;

 /**
  * UserApiController
  * <pre>
  *  사용자 관리 > 계정 및 권한 관리 API 컨트롤러.
  * </pre>
  *
  * @author nichefish
  */
 @RestController
 @RequiredArgsConstructor
 @Log4j2
 public class UserApiController
         extends BaseControllerImpl {

     @Getter
     private final String baseUrl = Url.USER_LIST;
     @Getter
     private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.USER;      // 작업 카테고리 (로그 적재용)

     private final UserService userService;
     private final AuthRoleService authRoleService;
     private final DtlCdService dtlCdService;

     /**
      * 사용자 관리 > 계정 및 권한 관리 > 사용자 아이디 중복 체크 (Ajax)
      * 사용자 계정 신청시 사용해야 하므로 인증 없이 접근 가능
      */
     @PostMapping(Url.USER_ID_DUP_CHK_AJAX)
     @ResponseBody
     public ResponseEntity<AjaxResponse> userIdDupChckAjax(
             final @RequestParam("userId") String userId,
             final LogActvtyParam logParam
     ) {

         final AjaxResponse ajaxResponse = new AjaxResponse();

         boolean isSuccess = false;
         String rsltMsg = "";
         try {
             // 중복여부 체크
             Boolean isUserIdDup = userService.userIdDupChck(userId);

             isSuccess = !isUserIdDup;
             rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_USER_ID_NOT_DUP : MessageUtils.RSLT_USER_ID_DUP);
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
      * 사용자 관리 > 계정 및 권한 관리 > 사용자 등록/수정 (Ajax)
      * (관리자MNGR만 접근 가능.)
      *
      * @param user 등록/수정 처리할 객체
      * @param key 식별자
      * @param logParam 로그 기록을 위한 파라미터 객체
      * @return {@link ResponseEntity} -- 처리 결과와 메시지
      */
     @PostMapping(value = {Url.USER_REG_AJAX, Url.USER_MDF_AJAX})
     @Secured(Constant.ROLE_MNGR)
     @ResponseBody
     public ResponseEntity<AjaxResponse> userRegAjax(
             final @Valid UserDto.DTL user,
             final @RequestParam("userNo") @Nullable Integer key,
             final LogActvtyParam logParam,
             final MultipartHttpServletRequest request
     ) {

         final AjaxResponse ajaxResponse = new AjaxResponse();

         boolean isSuccess = false;
         String rsltMsg = "";
         try {
             // 등록/수정 처리
             boolean isReg = key == null;
             UserDto result = isReg ? userService.regist(user, request) : userService.modify(user, request);
             ajaxResponse.setRsltObj(result);

             isSuccess = (result.getUserNo() != null);
             rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
         } catch (Exception e) {
             isSuccess = false;
             rsltMsg = MessageUtils.getExceptionMsg(e);
             logParam.setExceptionInfo(e);
         } finally {
             ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
             // 로그 관련 처리
             logParam.setCn(user.toString());
             logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
             publisher.publishEvent(new LogActvtyEvent(this, logParam));
         }

         return ResponseEntity
                 .status(HttpStatus.OK)
                 .body(ajaxResponse);
     }

     /**
      * 사용자 관리 > 계정 및 권한 관리 > 사용자 패스워드 초기화 (Ajax)
      * (관리자MNGR만 접근 가능.)
      */
     @PostMapping(Url.USER_PW_RESET_AJAX)
     @Secured(Constant.ROLE_MNGR)
     @ResponseBody
     public ResponseEntity<AjaxResponse> passwordResetAjax(
             final @RequestParam("userNo") Integer userNo,
             final LogActvtyParam logParam
     ) {

         final AjaxResponse ajaxResponse = new AjaxResponse();

         boolean isSuccess = false;
         String rsltMsg = "";
         try {
             // 패스워드 리셋 처리
             isSuccess = userService.passwordReset(userNo);
             rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS_PW_RESET : MessageUtils.RSLT_FAILURE);
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
      * 사용자 관리 > 계정 및 권한 관리 > 사용자 삭제 (Ajax)
      * (관리자MNGR만 접근 가능.)
      *
      * @param key 식별자
      * @param logParam 로그 기록을 위한 파라미터 객체
      * @return {@link ResponseEntity} -- 처리 결과와 메시지
      */
     @PostMapping(Url.USER_DEL_AJAX)
     @Secured(Constant.ROLE_MNGR)
     @ResponseBody
     public ResponseEntity<AjaxResponse> userDelAjax(
             final @RequestParam("userNo") Integer key,
             final LogActvtyParam logParam
     ) {

         final AjaxResponse ajaxResponse = new AjaxResponse();

         boolean isSuccess = false;
         String rsltMsg = "";
         try {
             UserDto rsUserDto = userService.getDtlDto(key);
             // 내 정보인지 비교 :: "내 정보는 삭제할 수 없습니다."
             if (AuthUtils.isMyInfo(rsUserDto.getUserId())) {
                 rsltMsg = MessageUtils.NOT_DELABLE_OWN_ID;
             } else {
                 // 삭제
                 isSuccess = userService.delete(key);
                 rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
             }
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
      * 사용자 관리 > 계정 및 권한 관리 > 사용자 목록 엑셀 다운로드
      * (관리자MNGR만 접근 가능.)
      * @param searchParam 검색 조건을 담은 파라미터 객체
      * @param logParam 로그 기록을 위한 파라미터 객체
      */
     @GetMapping(Url.USER_LIST_XLSX_DOWNLOAD)
     @Secured(Constant.ROLE_MNGR)
     public void userListXlsxDownload(
             @ModelAttribute("searchParam") UserSearchParam searchParam,
             final LogActvtyParam logParam
     ) throws Exception {

         boolean isSuccess = false;
         String rsltMsg = "";
         try {
             // List<Object> userListXlsx = userService.userListXlsx(searchParamMap);
             // xlsxUtils.listXlxsDownload(Constant.user_profl, userListXlsx);
             isSuccess = true;
             rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
         } catch (Exception e) {
             isSuccess = false;
             rsltMsg = MessageUtils.getExceptionMsg(e);
             logParam.setExceptionInfo(e);
             MessageUtils.alertMessage(rsltMsg, baseUrl);
         } finally {
             // 로그 관련 처리
             logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
             publisher.publishEvent(new LogActvtyEvent(this, logParam));
         }
     }
 }
