package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.admin.MenuDto;
import io.nicheblog.dreamdiary.web.model.admin.MenuListDto;
import io.nicheblog.dreamdiary.web.model.admin.MenuSearchParam;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.service.admin.MenuService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * MenuController
 * <pre>
 *  메뉴 관리 컨트롤러
 * </pre>
 * TODO: 신규개발 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@PreAuthorize("isAuthenticated()")
@Log4j2
public class MenuController
        extends BaseControllerImpl {

    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.MENU;        // 작업 카테고리 (로그 적재용)

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return actvtyCtgr.name();
    }

    @Resource(name = "menuService")
    private MenuService menuService;

    /**
     * 관리자 > 메뉴 관리 > 메뉴 관리 화면 조회
     */
    @RequestMapping(SiteUrl.MENU_LIST)
    public String menuList(
            @ModelAttribute("searchParam") MenuSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws IOException {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MENU.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String resultMsg = null;
        try {
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            resultMsg = MessageUtils.getExceptionMsg(e);
            MessageUtils.alertMessage(resultMsg, SiteUrl.ADMIN_MAIN);
        } finally {
            publisher.publishEvent(new LogActvtyEvent(this, logParam));        // 활동 로그
            log.info("isSuccess: " + isSuccess + ", resultMsg: " + resultMsg);
        }

        return "/view/admin/menu/menu_list";
    }

    /**
     * 메뉴 관리 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {SiteUrl.MENU_REG_AJAX, SiteUrl.MENU_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> menuRegAjax(
            final @Valid MenuDto menuDto,
            final Integer menuId,
            final LogActvtyParam logParam,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            boolean isReg = menuDto.getMenuId() == null;
            MenuDto result = isReg ? menuService.regist(menuDto) : menuService.modify(menuDto, menuId);
            isSuccess = result.getMenuId() != null;
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(menuDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 메뉴 관리 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.MENU_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> menuDtlAjax(
            final @RequestParam("menuId") String menuIdStr,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 게시판 정보 조회
            Integer menuId = Integer.parseInt(menuIdStr);
            MenuDto rsDto = menuService.getDtlDto(menuId);
            ajaxResponse.setResultObj(rsDto);
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + menuIdStr);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }


    /**
     * 메뉴 관리 (메인) 목록 조회 (Ajax)
     * (사용자USER, )관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.MENU_MAIN_LIST_AJAX)
    @Secured({Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> menuListAjax(
            @ModelAttribute("searchParam") MenuSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 팝업공지 목록 조회
            Sort sort = Sort.by(Sort.Direction.ASC, "sortOrdr");
            PageRequest pageRequest = CmmUtils.Param.getPageRequest(searchParam, sort, model);
            Page<MenuListDto> menuList = menuService.getMainMenuList(searchParam, pageRequest);
            ajaxResponse.setResultList(menuList.getContent());
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 반복적으로 호출되므로 실패(Exception)시 외에는 로그 적재하지 않음
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 메뉴 관리 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.MENU_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> menuDelAjax(
            final @RequestParam("menuId") String menuIdStr,
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer menuId = Integer.parseInt(menuIdStr);
            isSuccess = menuService.delete(menuId);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + menuIdStr);
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
