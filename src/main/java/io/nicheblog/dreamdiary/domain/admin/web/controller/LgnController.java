package io.nicheblog.dreamdiary.domain.admin.web.controller;

import io.nicheblog.dreamdiary.domain.admin.menu.SiteMenu;
import io.nicheblog.dreamdiary.domain.user.info.model.UserPwChgParam;
import io.nicheblog.dreamdiary.domain.user.my.service.UserMyService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * LgnController
 * <pre>
 *  로그인 컨트롤러.
 * </pre>
 *
 * @author nichefish
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
     *
     * @param dupLgnAt 중복 로그인 여부를 나타내는 파라미터 (nullable)
     * @param model 뷰에 데이터를 전달하는 ModelMap 객체
     * @return {@link String} -- 로그인 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.AUTH_LGN_FORM)
    @PermitAll
    public String lgnForm(
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

        return "/view/global/_common/auth/lgn_form";
    }

    /**
     * GET으로 로그인 처리/로그아웃 페이지 접근시 로그인 화면으로 리다이렉트
     */
    @GetMapping({ Url.AUTH_LGN_PROC, Url.AUTH_LGOUT})
    public String authRedirection() {

        log.info("'GET' access in lgnProc!");
        return "redirect:" + Url.AUTH_LGN_FORM;
    }

    /**
     * 비밀번호 강제 변경 처리 (장기간 비밀번호 미변경시, 또는 비밀번호 리셋시)
     * (비로그인 사용자도 외부에서 접근 가능)
     *
     * @param userPwChgParam 비밀번호 변경을 위한 파라미터 객체 (유효성 검사 적용)
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.AUTH_LGN_PW_CHG_AJAX)
    @PermitAll
    @ResponseBody
    public ResponseEntity<AjaxResponse> lgnPwChgAjax(
            final @Valid UserPwChgParam userPwChgParam,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 비밀번호 변경 처리
            isSuccess = userMyService.lgnPwChg(userPwChgParam);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
        } finally {
            // 로그 관련 세팅
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 세션 강제 만료 처리 (중복 로그인 '기존 아이디 끊기'에서 취소 선택시)
     *
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.AUTH_EXPIRE_SESSION_AJAX)
    @PermitAll
    @ResponseBody
    public ResponseEntity<AjaxResponse> expireSessionAjax(
            //
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        // 세션 만료 처리
        final HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        ajaxResponse.setAjaxResult(true, MessageUtils.RSLT_SUCCESS);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
}
