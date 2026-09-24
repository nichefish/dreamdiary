package io.nicheblog.dreamdiary.feature.prospective.project.controller;

import io.nicheblog.dreamdiary.feature.prospective.project.model.ProspectiveProjectDto;
import io.nicheblog.dreamdiary.feature.prospective.project.service.ProspectiveProjectService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.infrastructure.log.type.ActvtyCtgr;
import io.nicheblog.dreamdiary.infrastructure.web.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.infrastructure.web.model.AjaxResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/** Prospective Project 사용자 소유 REST API. */
@RestController
@RequiredArgsConstructor
public class ProspectiveProjectRestController extends BaseControllerImpl {

    @Getter
    private final String baseUrl = "/keep";
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.JOURNAL;

    private final ProspectiveProjectService prospectiveProjectService;

    /** 내 Project 목록 조회. */
    @GetMapping(Url.PROSPECTIVE_PROJECTS)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public ResponseEntity<AjaxResponse> list() {
        return ResponseEntity.ok(AjaxResponse.withAjaxResult(true, MessageUtils.getMessage("common.result.success"))
                .withList(prospectiveProjectService.getMyList()));
    }

    /** 내 Project 상세 조회. */
    @GetMapping(Url.PROSPECTIVE_PROJECT)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public ResponseEntity<AjaxResponse> detail(final @PathVariable("id") Integer id) throws Exception {
        return ResponseEntity.ok(AjaxResponse.withAjaxResult(true, MessageUtils.getMessage("common.result.success"))
                .withObj(prospectiveProjectService.getMyDetail(id)));
    }

    /** Project 등록. */
    @PostMapping(Url.PROSPECTIVE_PROJECTS)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public ResponseEntity<AjaxResponse> regist(final @Valid ProspectiveProjectDto dto) throws Exception {
        final ServiceResponse result = prospectiveProjectService.regist(dto);
        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, MessageUtils.getMessage("common.result.success")));
    }

    /** Project 수정. */
    @PostMapping(Url.PROSPECTIVE_PROJECT)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public ResponseEntity<AjaxResponse> modify(
            final @PathVariable("id") Integer id,
            final @Valid ProspectiveProjectDto dto
    ) throws Exception {
        dto.setId(id);
        final ServiceResponse result = prospectiveProjectService.modify(dto);
        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, MessageUtils.getMessage("common.result.success")));
    }

    /** Project soft-delete. */
    @DeleteMapping(Url.PROSPECTIVE_PROJECT)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public ResponseEntity<AjaxResponse> delete(final @PathVariable("id") Integer id) throws Exception {
        final ServiceResponse result = prospectiveProjectService.delete(id);
        return ResponseEntity.ok(AjaxResponse.fromResponse(result, MessageUtils.getMessage("common.result.success")));
    }

    /** 내 활성 Project 전체 순서 변경. */
    @PutMapping(Url.PROSPECTIVE_PROJECT_SORT_ORDERS)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public ResponseEntity<AjaxResponse> reorder(final @RequestBody List<Integer> orderedIds) {
        prospectiveProjectService.reorderMyProjects(orderedIds);
        return ResponseEntity.ok(AjaxResponse.withAjaxResult(true, MessageUtils.getMessage("common.result.success")));
    }
}
