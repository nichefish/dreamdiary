package io.nicheblog.dreamdiary.global._common._clsf.tag.controller;

import io.nicheblog.dreamdiary.domain.admin.menu.SiteMenu;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common._clsf.tag.model.TagSearchParam;
import io.nicheblog.dreamdiary.global._common._clsf.tag.service.TagService;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * TagApiController
 * <pre>
 *  태그 관리 페이지 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class TagPageController
        extends BaseControllerImpl {

    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.TAG;           // 작업 카테고리 (로그 적재용)

    private final TagService tagService;

    /**
     * 태그 관리 화면 조회
     * (관리자MNGR만 접근 가능.)
     *
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @GetMapping(Url.TAG_LIST)
    @Secured({Constant.ROLE_MNGR})
    public String tagList(
            @ModelAttribute("searchParam") TagSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.TAG.setAcsPageInfo(Constant.PAGE_LIST));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 관련 컨텐츠 타입 목록 조회
            model.addAttribute("contentTypeList", tagService.getContentTypeList());
            // 활성 컨텐츠 타입 모델에 추가
            model.addAttribute("refContentType", searchParam.getRefContentType());

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.ADMIN_MAIN);
        } finally {
            // 로그 관련 세팅
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/domain/admin/tag/tag_list";
    }
}
