package io.nicheblog.dreamdiary.web.controller;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.user.UserPwChgParam;
import io.nicheblog.dreamdiary.web.service.user.UserMyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.InvalidParameterException;

/**
 * LgnController
 * <pre>
 *  로그인 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class LgnController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.AUTH_LGN_FORM;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.LGN;      // 작업 카테고리 (로그 적재용)

    private final UserMyService userMyService;

    @Value("${remember-me.param}")
    private String REMEMBER_ME_PARAM;

    /**
     * 로그인 화면 조회
     */
    @GetMapping(Url.AUTH_LGN_FORM)
    @PermitAll
    public String lgnForm(
            final LogActvtyParam logParam,
            final @RequestParam("dupLgnAt") @Nullable String dupLgnAt,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.LGN_PAGE.setAcsPageInfo(Constant.PAGE_LGN));

        // 로그인 상태일 경우:: 메인 화면으로 리다이렉트
        if (AuthUtils.isAuthenticated()) return "redirect:" + Url.MAIN;

        // remember-me 관련 파라미터 세팅
        model.addAttribute("REMEMBER_ME_PARAM", REMEMBER_ME_PARAM);

        // 중복 로그인으로 인해 로그인 면으로 튕겨나왔을 경우 alert
        if ("Y".equals(dupLgnAt)) MessageUtils.alertMessage("중복 로그인 방지에 의해 로그아웃 처리되었습니다.", Url.AUTH_LGN_FORM);

        return "/view/lgn_form";
    }

    /**
     * GET으로 로그인 처리/로그아웃 페이지 접근시 로그인 화면으로 리다이렉트
     */
    @GetMapping({ Url.AUTH_LGN_PROC, Url.AUTH_LGOUT})
    public String authRedirection() {

        log.info("'get' access in lgnProc!");
        return "redirect:" + Url.AUTH_LGN_FORM;
    }

    /**
     * 비밀번호 강제 변경 처리 (장기간 비밀번호 미변경시, 또는 비밀번호 리셋시)
     * (비로그인 사용자도 외부에서 접근 가능)
     */
    @PostMapping(Url.AUTH_LGN_PW_CHG_AJAX)
    @PermitAll
    @ResponseBody
    public ResponseEntity<AjaxResponse> lgnPwChgAjax(
            final LogActvtyParam logParam,
            final @Valid UserPwChgParam userPwChgParam,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // Validation
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            // 비밀번호 변경 처리
            isSuccess = userMyService.lgnPwChg(userPwChgParam);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
        } finally {
            // 로그 관련 처리
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 세션 강제 만료 처리 (중복 로그인 '기존 아이디 끊기'에서 취소 선택시)
     */
    @PostMapping(Url.AUTH_EXPIRE_SESSION_AJAX)
    @PermitAll
    @ResponseBody
    public ResponseEntity<AjaxResponse> expireSessionAjax(
            //
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        // 세션 만료 처리
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        ajaxResponse.setAjaxResult(true, MessageUtils.RSLT_SUCCESS);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
}
