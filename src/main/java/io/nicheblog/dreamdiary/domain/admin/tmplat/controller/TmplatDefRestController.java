package io.nicheblog.dreamdiary.domain.admin.tmplat.controller;

import io.nicheblog.dreamdiary.domain.admin.tmplat.model.TmplatDefDto;
import io.nicheblog.dreamdiary.domain.admin.tmplat.service.TmplatDefService;
import io.nicheblog.dreamdiary.extension.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.extension.log.actvty.aspect.LogActvtyRestControllerAspect;
import io.nicheblog.dreamdiary.extension.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * TmplatDefRestController
 * <pre>
 *  템플릿 정의 관리 API 컨트롤러.
 * </pre>
 * TODO: 신규개발 예정
 *
 * @author nichefish
 * @see LogActvtyRestControllerAspect
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class TmplatDefRestController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.TMPLAT_DEF_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.TMPLAT;        // 작업 카테고리 (로그 적재용)

    private final TmplatDefService tmplatDefService;

    /**
     * 템플릿 정의 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param tmplatDef 등록/수정 처리할 객체
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @PostMapping(value = {Url.TMPLAT_DEF_REG_AJAX, Url.TMPLAT_DEF_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> tmplatDefRegAjax(
            final @Valid TmplatDefDto tmplatDef,
            final LogActvtyParam logParam
    ) throws Exception {

        final boolean isReg = (tmplatDef.getKey() == null);
        final ServiceResponse result = isReg ? tmplatDefService.regist(tmplatDef) : tmplatDefService.modify(tmplatDef);
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }
}
