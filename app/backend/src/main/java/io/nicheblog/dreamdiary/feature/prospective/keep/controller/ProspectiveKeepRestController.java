package io.nicheblog.dreamdiary.feature.prospective.keep.controller;

import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepDto;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepSearchParam;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepProjectRequest;
import io.nicheblog.dreamdiary.feature.prospective.keep.service.ProspectiveKeepService;
import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.tag.service.TagService;
import io.nicheblog.dreamdiary.feature.prospective.keep.service.my.MyProspectiveKeepService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.infrastructure.log.type.ActvtyCtgr;
import io.nicheblog.dreamdiary.infrastructure.web.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.infrastructure.web.model.AjaxResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * ProspectiveKeepRestController
 * <pre>
 *  Prospective Keep RestController.
 * </pre>
 *
 * @author nichefish
 */
@RestController
@RequiredArgsConstructor
public class ProspectiveKeepRestController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.JOURNAL_DAY_MONTHLY;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.JOURNAL;        // 작업 카테고리 (로그 적재용)

    private final ProspectiveKeepService prospectiveKeepService;
    private final MyProspectiveKeepService myProspectiveKeepService;
    private final TagService tagService;

    /**
     * Prospective Keep 태그 카테고리 맵 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     * <pre>
     *  등록/수정 모달의 Tagify 카테고리 자동완성이 사용한다.
     * </pre>
     *
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @GetMapping(value = {Url.PROSPECTIVE_KEEP_TAG_CATEGORIES})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> prospectiveKeepTagCategoryMapAjax(
            //
    ) throws Exception {

        final Map<String, List<String>> tagCategoryMap = tagService.getMyTagCategoryMap(ContentType.PROSPECTIVE_KEEP.key);
        final boolean isSuccess = true;
        final String rsltMsg = MessageUtils.getMessage("common.result.success");

        return ResponseEntity.ok(AjaxResponse.withAjaxResult(isSuccess, rsltMsg).withMap(tagCategoryMap));
    }

    /**
     * Prospective Keep 목록 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     * <pre>
     *  scope=all 이면 RESOLVED 포함 전 범위(keep 전수관리용), 그 외에는 활성(OPEN·PENDING) 집합을 반환한다.
     * </pre>
     *
     * @param scope 조회 범위 (all=전 범위, 그 외/미지정=활성)
     * @param searchParam 검색 조건을 담은 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @GetMapping(value = {Url.PROSPECTIVE_KEEPS})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> prospectiveKeepListAjax(
            final @RequestParam(value = "scope", required = false) String scope,
            final ProspectiveKeepSearchParam searchParam
    ) throws Exception {

        final List<ProspectiveKeepDto> prospectiveKeepList = "all".equals(scope)
                ? myProspectiveKeepService.getMyList(searchParam)
                : myProspectiveKeepService.getMyActiveList(searchParam);
        final boolean isSuccess = true;
        final String rsltMsg = MessageUtils.getMessage("common.result.success");

        return ResponseEntity.ok(AjaxResponse.withAjaxResult(isSuccess, rsltMsg).withList(prospectiveKeepList));
    }

    /**
     * Prospective Keep 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param prospectiveKeep 등록/수정 처리할 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @Operation(
            summary = "Prospective Keep 등록/수정",
            description = "Prospective Keep 정보를 등록/수정한다."
    )
    @PostMapping(value = {Url.PROSPECTIVE_KEEPS, Url.PROSPECTIVE_KEEP})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> prospectiveKeepRegistAjax(
            final @PathVariable(value = "id", required = false) Integer id,
            final @Valid ProspectiveKeepDto prospectiveKeep
    ) throws Exception {

        final boolean isModify = (id != null);
        if (isModify) prospectiveKeep.setId(id);
        final ServiceResponse result = isModify ? prospectiveKeepService.modify(prospectiveKeep) : prospectiveKeepService.regist(prospectiveKeep);
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = isSuccess ? MessageUtils.getMessage("common.result.success") : MessageUtils.getMessage("common.result.failure");

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }

    /**
     * Prospective Keep 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     * <pre>
     *  목록과 같은 lifecycle(행이 없으면 OPEN)을 부착해 반환한다.
     * </pre>
     *
     * @param id 식별자
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @GetMapping(value = {Url.PROSPECTIVE_KEEP})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> prospectiveKeepDetailAjax(
            final @PathVariable("id") Integer id
    ) throws Exception {

        final ProspectiveKeepDto retrievedDto = myProspectiveKeepService.getMyDetailDtoWithLifecycle(id);
        final boolean isSuccess = (retrievedDto.getId() != null);
        final String rsltMsg = MessageUtils.getMessage("common.result.success");

        return ResponseEntity.ok(AjaxResponse.withAjaxResult(isSuccess, rsltMsg).withObj(retrievedDto));
    }

    /** Keep의 단일 Project 소속만 변경한다. null은 Project 미지정으로 이동한다. */
    @PatchMapping(value = {Url.PROSPECTIVE_KEEP_PROJECT})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> prospectiveKeepProjectModifyAjax(
            final @PathVariable("id") Integer id,
            final @RequestBody ProspectiveKeepProjectRequest request
    ) throws Exception {
        prospectiveKeepService.moveProject(id, request.getProjectId());
        return ResponseEntity.ok(AjaxResponse.withAjaxResult(
                true, MessageUtils.getMessage("common.result.success")));
    }

    /**
     * Prospective Keep 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param id 식별자
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @DeleteMapping(value = {Url.PROSPECTIVE_KEEP})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> prospectiveKeepDeleteAjax(
            final @PathVariable("id") Integer id
    ) throws Exception {

        final ServiceResponse result = prospectiveKeepService.delete(id);
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = MessageUtils.getMessage("common.result.success");

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }
}
