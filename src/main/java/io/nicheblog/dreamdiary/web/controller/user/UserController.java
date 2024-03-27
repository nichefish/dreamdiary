package io.nicheblog.dreamdiary.web.controller.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.CmmUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.PaginationInfo;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.UserListDto;
import io.nicheblog.dreamdiary.web.model.user.UserSearchParam;
import io.nicheblog.dreamdiary.web.service.user.UserService;
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

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;
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

    private final String baseUrl = SiteUrl.USER_LIST;               // 기본 URL
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.USER;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "userService")
    private UserService userService;

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
            final @ModelAttribute("searchParam") UserSearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("사용자 목록 조회"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            Map<String, Object> listParamMap = CmmUtils.checkPrevSearchMap(searchParamMap, baseUrl, searchParam);

            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            Sort sort = Sort.by(Sort.Direction.ASC, "acntStus.cfYn")
                            .and(Sort.by(Sort.Direction.ASC, "acntStus.lockedYn"))
                            .and(Sort.by(Sort.Direction.DESC, "regDt"));
            PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, sort, model);
            Page<UserListDto> userList = userService.getListDto(listParamMap, pageRequest);
            if (userList != null) model.addAttribute("userList", userList.getContent());
            model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(userList));
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            cdService.setModelCdData(Constant.AUTH_CD, model);
            cdService.setModelCdData(Constant.TEAM_CD, model);
            cdService.setModelCdData(Constant.EMPLYM_CD, model);
            cdService.setModelCdData(Constant.JOB_TITLE_CD, model);

            CmmUtils.setModelAttrMap(listParamMap, searchParam, baseUrl, model);        // 검색 파라미터 다시 모델에 추가
            // 관리자페이지 화면 모드 세팅
            session.setAttribute("userMode", Constant.AUTH_MNGR);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, SiteUrl.ADMIN_MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
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
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("사용자 등록"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            model.addAttribute("user", new UserDto());      // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute(Constant.IS_REG, true);           // 등록/수정 화면 플래그 세팅
            cdService.setModelCdData(Constant.AUTH_CD, model);
            cdService.setModelCdData(Constant.TEAM_CD, model);
            cdService.setModelCdData(Constant.EMPLYM_CD, model);
            cdService.setModelCdData(Constant.JOB_TITLE_CD, model);

            // 관리자페이지 화면 모드 세팅
            session.setAttribute("userMode", Constant.AUTH_MNGR);

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
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
            final LogActvtyParam logParam,
            final @RequestParam("userId") String userId
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Boolean isUserIdDup = userService.userIdDupChck(userId);
            isSuccess = !isUserIdDup;
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_USER_ID_NOT_DUP : MessageUtils.RSLT_USER_ID_DUP);
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

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 등록/수정 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {SiteUrl.USER_REG_AJAX, SiteUrl.USER_MDF_AJAX})
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> userRegAjax(
            final @Valid UserDto userDto,
            final Integer userNo,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = userDto.getUserNo() == null;
            UserDto result = isReg ? userService.regist(userDto, request) : userService.modify(userDto, userNo, request);
            isSuccess = (result.getUserNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(userDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
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
            final @RequestParam("userNo") String userNoStr,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("사용자 상세 조회"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer userNo = Integer.parseInt(userNoStr);
            UserDto rsUserDto = userService.getDtlDto(userNo);
            model.addAttribute("user", rsUserDto);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 관리자페이지 화면 모드 세팅
            session.setAttribute("userMode", Constant.AUTH_MNGR);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + userNoStr);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
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
            final LogActvtyParam logParam,
            final @RequestParam("userNo") String userNoStr,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("사용자 수정"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer userNo = Integer.parseInt(userNoStr);
            UserDto rsUserDto = userService.getDtlDto(userNo);
            model.addAttribute("user", rsUserDto);

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);

            model.addAttribute(Constant.IS_MDF, true);       // 등록/수정 화면 플래그
            cdService.setModelCdData(Constant.AUTH_CD, model);
            cdService.setModelCdData(Constant.TEAM_CD, model);
            cdService.setModelCdData(Constant.EMPLYM_CD, model);
            cdService.setModelCdData(Constant.JOB_TITLE_CD, model);

            // 관리자페이지 화면 모드 세팅
            session.setAttribute("userMode", Constant.AUTH_MNGR);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setCn("key: " + userNoStr);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
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
            final LogActvtyParam logParam,
            final @RequestParam("userNo") String userNoStr
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer userNo = Integer.parseInt(userNoStr);
            isSuccess = userService.passwordReset(userNo);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS_PW_RESET : MessageUtils.RSLT_FAILURE);
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

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 삭제 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.USER_DEL_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> userDelAjax(
            final LogActvtyParam logParam,
            final @RequestParam("userNo") String userNoStr
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer userNo = Integer.parseInt(userNoStr);
            UserDto rsUserDto = userService.getDtlDto(userNo);
            // 내 정보인지 비교
            if (AuthUtils.isMyInfo(rsUserDto.getUserId())) {
                isSuccess = false;
                resultMsg = MessageUtils.NOT_DELABLE_OWN_ID;
            } else {
                isSuccess = userService.delete(userNo);
                resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            }
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

    /**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 목록 엑셀 다운로드
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.USER_LIST_XLSX_DOWNLOAD)
    @Secured(Constant.ROLE_MNGR)
    public void userListXlsxDownload(
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") UserSearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap
    ) throws Exception {

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // List<Object> userListXlsx = userService.userListXlsx(searchParamMap);
            // xlsxUtils.listXlxsDownload(Constant.user_profl, userListXlsx);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
    }
}
