package io.nicheblog.dreamdiary.web.controller.log;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.log.LogStatsSearchParam;
import io.nicheblog.dreamdiary.web.model.log.LogStatsUserDto;
import io.nicheblog.dreamdiary.web.service.log.LogStatsUserService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * LogStatsUserController
 * <pre>
 *  로그 관리 > 활동 로그 관리 컨트롤러
 * </pre>
 * TODO: 통계 고도화 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class LogStatsController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.LOG_STATS_USER_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.LOG_STATS;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "logStatsUserService")
    private LogStatsUserService logStatsUserService;

    /**
     * 활동 로그 > 활동 로그 목록 (전체) 화면 조회
     * 관리자MNGR만 접근 가능
     */
    @GetMapping(SiteUrl.LOG_STATS_USER_LIST)
    @Secured(Constant.ROLE_MNGR)
    public String logStatsUserList(
            @ModelAttribute("searchParam") LogStatsSearchParam searchParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws IOException {

        /* 사이트 메뉴 설정 */
        // siteMenuAcsInfo.setAcsInfo(SiteMenu.MENU_LOG_STATS, SiteMenu.PAGE_LIST, request.getRequestURI());

        // 활동 로그 목록 조회
        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
            searchParam = (LogStatsSearchParam) CmmUtils.Param.checkPrevSearchParam(baseUrl, searchParam);
            // 목록 조회
            List<LogStatsUserDto> logStatsUserList = logStatsUserService.logStatsUserDtoList(searchParam, Pageable.unpaged());
            if (logStatsUserList != null) model.addAttribute("logStatsUserList", logStatsUserList);
            List<LogStatsUserDto> logStatsNotUserList = logStatsUserService.logStatsNotUserDtoList(searchParam, Pageable.unpaged());
            if (logStatsUserList != null) model.addAttribute("logStatsNotUserList", logStatsNotUserList);
            // 목록 검색 URL + 파라미터 모델에 추가
            CmmUtils.Param.setModelAttrMap(searchParam, baseUrl, model);

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
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

        return "/view/log/stats/log_stats_user_list";
    }
}
