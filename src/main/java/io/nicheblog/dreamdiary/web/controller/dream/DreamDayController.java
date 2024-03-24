package io.nicheblog.dreamdiary.web.controller.dream;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.CmmUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.user.UserSearchParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * DreamController
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
public class DreamDayController
        extends BaseControllerImpl {

    private final String baseUrl = SiteUrl.DREAM_DAY_LIST;               // 기본 URL
    private final ActvtyCtgr actvtyCtgrCd = ActvtyCtgr.DREAM;        // 작업 카테고리 (로그 적재용)


    /**
     * 꿈 관리 - 목록 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(SiteUrl.DREAM_DAY_LIST)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String dreamList(
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") UserSearchParam searchParam,
            final @RequestParam Map<String, Object> searchParamMap,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("꿈 목록 조회"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            Map<String, Object> listParamMap = CmmUtils.checkPrevSearchMap(searchParamMap, baseUrl, searchParam);

            // 페이징 정보 생성:: 공백시 pageSize=10, pageNo=1
            Sort sort = Sort.by(Sort.Direction.ASC, "cfYn")
                    .and(Sort.by(Sort.Direction.ASC, "lockYn"))
                    .and(Sort.by(Sort.Direction.DESC, "regDt"));
            PageRequest pageRequest = CmmUtils.getPageRequest(listParamMap, sort, model);
            // Page<UserListDto> userList = userService.getListDto(listParamMap, pageRequest);
            // if (userList != null) model.addAttribute("userList", userList.getContent());
            // model.addAttribute(Constant.PAGINATION_INFO, new PaginationInfo(userList));
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);

            // 검색 파라미터 다시 모델에 추가
            CmmUtils.setModelAttrMap(listParamMap, searchParam, baseUrl, model);
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

        return "/view/dream/day/dream_day_list";
    }

}
