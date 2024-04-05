package io.nicheblog.dreamdiary.web.controller.dream;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.dream.piece.DreamPieceDto;
import io.nicheblog.dreamdiary.web.service.dream.DreamPieceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;

/**
 * DreamPieceController
 * <pre>
 *  꿈 조각 Controller
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
public class DreamPieceController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.DREAM_DAY_PAGE;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.DREAM;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "dreamPieceService")
    private DreamPieceService dreamPieceService;

    /**
     * 꿈 조각 등록/수정 처리 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @Operation(
            summary = "꿈 조각 등록/수정",
            description = "꿈 조각 정보를 등록/수정한다."
    )
    @PostMapping(value = {SiteUrl.DREAM_PIECE_REG_AJAX, SiteUrl.DREAM_PIECE_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> dreamPieceRegAjax(
            final @Valid DreamPieceDto dreamPiece,
            final @RequestParam("postNo") @Nullable Integer postNo,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // Validation
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            // 등록 및 수정 처리
            boolean isReg = dreamPiece.getPostNo() == null;
            DreamPieceDto result = isReg ? dreamPieceService.regist(dreamPiece, request) : dreamPieceService.modify(dreamPiece, postNo, request);

            isSuccess = (result.getPostNo() != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(dreamPiece.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

}
