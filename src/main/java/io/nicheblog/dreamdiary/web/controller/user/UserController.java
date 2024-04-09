package io.nicheblog.dreamdiary.web.controller.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.service.AuthRoleService;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.UserSearchParam;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * UserInfoController
 * <pre>
 *  사용자 관리 > 계정 및 권한 관리 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class UserController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.USER_LIST;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.USER;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "authRoleService")
    private AuthRoleService authRoleService;
    @Resource(name = "cdService")
    private CdService cdService;

    // @Resource(name = "xlsxUtils")
    // private XlsxUtils xlsxUtils;

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 목록 화면 조회
     * (관리자MNGR만 접근 가능)
     */
    @GetMapping(SiteUrl.USER_LIST)
    @Secured(Constant.ROLE_MNGR)
    public String userList(
            @ModelAttribute("searchParam") UserSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.USER_INFO.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            searchParam = (UserSearchParam) CmmUtils.Param.checkPrevSearchParam(baseUrl, searchParam);
            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            Sort sort = Sort.by(Sort.Direction.ASC, "acntStus.cfYn")
                            .and(Sort.by(Sort.Direction.ASC, "acntStus.lockedYn"))
                            .and(Sort.by(Sort.Direction.DESC, "regDt"));
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, sort, model);
            // 목록 조회
            Page<UserDto.LIST> userList = userService.getPageDto(searchParam, pageRequest);
            model.addAttribute("userList", userList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(userList));
            // 코드 정보 모델에 추가
            cdService.setModelCdData(Constant.AUTH_CD, model);
            cdService.setModelCdData(Constant.TEAM_CD, model);
            cdService.setModelCdData(Constant.EMPLYM_CD, model);
            cdService.setModelCdData(Constant.JOB_TITLE_CD, model);
            // 목록 검색 URL + 파라미터 모델에 추가
            CmmUtils.Param.setModelAttrMap(searchParam, baseUrl, model);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, SiteUrl.ADMIN_MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/user/info/user_list";
    }

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 등록 화면 조회
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.USER_REG_FORM)
    @Secured(Constant.ROLE_MNGR)
    public String userRegForm(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.USER_INFO.setAcsPageInfo("사용자 등록"));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute("user", new UserDto());
            // 등록/수정 화면 플래그 세팅
            model.addAttribute(Constant.IS_REG, true);
            // 권한 정보 모델에 추가
            Map<String, Object> searchParamMap = new HashMap<>() {{
                put("useYn", "Y");
            }};
            model.addAttribute("authRoleList", authRoleService.getListDto(searchParamMap));
            // 코드 정보 모델에 추가
            cdService.setModelCdData(Constant.AUTH_CD, model);
            cdService.setModelCdData(Constant.TEAM_CD, model);
            cdService.setModelCdData(Constant.EMPLYM_CD, model);
            cdService.setModelCdData(Constant.JOB_TITLE_CD, model);

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

        return "/view/user/info/user_reg_form";
    }

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 아이디 중복 체크 (Ajax)
     * 사용자 계정 신청시 사용해야 하므로 인증 없이 접근 가능
     */
    @PostMapping(SiteUrl.USER_ID_DUP_CHK_AJAX)
    @ResponseBody
    public ResponseEntity<AjaxResponse> userIdDupChckAjax(
            final @RequestParam("userId") String userId,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

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

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 등록/수정 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {SiteUrl.USER_REG_AJAX, SiteUrl.USER_MDF_AJAX})
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> userRegAjax(
            final @Valid UserDto.DTL user,
            final @RequestParam("userNo") @Nullable Integer userNo,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // Validation
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            // 등록/수정 처리
            boolean isReg = user.getUserNo() == null;
            UserDto result = isReg ? userService.regist(user, request) : userService.modify(user, userNo, request);

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

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 상세 화면 조회
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.USER_DTL)
    @Secured(Constant.ROLE_MNGR)
    public String userDtl(
            final @RequestParam("userNo") Integer userNo,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.USER_INFO.setAcsPageInfo("사용자 상세 조회"));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상세 조회 및 모델에 추가
            UserDto rsUserDto = userService.getDtlDto(userNo);
            model.addAttribute("user", rsUserDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + userNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/user/info/user_dtl";
    }

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 수정 화면 조회
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.USER_MDF_FORM)
    @Secured(Constant.ROLE_MNGR)
    public String userMdfForm(
            final @RequestParam("userNo") Integer userNo,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.USER_INFO.setAcsPageInfo("사용자 수정"));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 상세 조회 및 모델에 추가
            UserDto rsUserDto = userService.getDtlDto(userNo);
            model.addAttribute("user", rsUserDto);
            // 등록/수정 화면 플래그
            model.addAttribute(Constant.IS_MDF, true);
            // 권한 정보 모델에 추가
            Map<String, Object> searchParamMap = new HashMap<>() {{
                put("useYn", "Y");
            }};
            model.addAttribute("authRoleList", authRoleService.getListDto(searchParamMap));
            // 코드 정보 모델에 추가
            cdService.setModelCdData(Constant.AUTH_CD, model);
            cdService.setModelCdData(Constant.TEAM_CD, model);
            cdService.setModelCdData(Constant.EMPLYM_CD, model);
            cdService.setModelCdData(Constant.JOB_TITLE_CD, model);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);

        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + userNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/user/info/user_reg_form";
    }

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 패스워드 초기화
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.USER_PW_RESET_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> passwordResetAjax(
            final @RequestParam("userNo") Integer userNo,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

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
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 삭제 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.USER_DEL_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> userDelAjax(
            final @RequestParam("userNo") Integer userNo,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            UserDto rsUserDto = userService.getDtlDto(userNo);
            // 내 정보인지 비교 :: "내 정보는 삭제할 수 없습니다."
            if (AuthUtils.isMyInfo(rsUserDto.getUserId())) {
                rsltMsg = MessageUtils.NOT_DELABLE_OWN_ID;
            } else {
                // 삭제 처리
                isSuccess = userService.delete(userNo);
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

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 목록 엑셀 다운로드
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.USER_LIST_XLSX_DOWNLOAD)
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
