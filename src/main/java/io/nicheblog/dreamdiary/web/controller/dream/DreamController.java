package io.nicheblog.dreamdiary.web.controller.dream;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * DreamController
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
public class DreamController
        extends BaseControllerImpl {

    private final String baseUrl = SiteUrl.USER_LIST;               // 기본 URL
    private final ActvtyCtgr actvtyCtgrCd = ActvtyCtgr.USER;        // 작업 카테고리 (로그 적재용)

}
