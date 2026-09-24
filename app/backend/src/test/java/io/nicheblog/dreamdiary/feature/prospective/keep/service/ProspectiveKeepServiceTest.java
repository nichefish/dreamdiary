package io.nicheblog.dreamdiary.feature.prospective.keep.service;

import io.nicheblog.dreamdiary.auth.security.config.TestAuditConfig;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepDto;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepDtoTestFactory;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepSearchParam;
import io.nicheblog.dreamdiary.feature.prospective.project.model.ProspectiveProjectDto;
import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.feature.prospective.project.repository.jpa.ProspectiveProjectRepository;
import io.nicheblog.dreamdiary.feature.prospective.project.service.ProspectiveProjectService;
import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProspectiveKeepServiceTest
 * <pre>
 *  Service-level CRUD tests for ProspectiveKeep.
 *  Tests run in transaction and roll back after each case.
 * </pre>
 *
 * @author nichefish
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestAuditConfig.class)
@Transactional
@WithMockUser(username = TestConstant.TEST_AUDITOR)
class ProspectiveKeepServiceTest {

    @Resource
    private ProspectiveKeepService prospectiveKeepService;
    @Resource
    private ProspectiveProjectService prospectiveProjectService;
    @Resource
    private ProspectiveProjectRepository prospectiveProjectRepository;

    private ProspectiveKeepDto prospectiveKeep;

    /**
     * Initialize test data before each case.
     */
    @BeforeEach
    void setUp() throws Exception {
        prospectiveKeep = ProspectiveKeepDtoTestFactory.create();
    }

    /**
     * Create keep.
     */
    @Test
    void regist() throws Exception {
        // Given::

        // When::
        final ServiceResponse registResult = prospectiveKeepService.regist(prospectiveKeep);
        final ProspectiveKeepDto registered = (ProspectiveKeepDto) registResult.getRsltObj();

        // Then::
        assertNotNull(registered, "Register result is null.");
        assertNotNull(registered.getId(), "Register did not assign key.");
        assertEquals(1, registered.getSortOrder(), "Unexpected index for first item.");
    }

    /**
     * Update keep.
     */
    @Test
    void modify() throws Exception {
        // Given::
        final ServiceResponse registResult = prospectiveKeepService.regist(prospectiveKeep);
        final ProspectiveKeepDto registered = (ProspectiveKeepDto) registResult.getRsltObj();
        final Integer key = registered.getKey();

        // When::
        final ProspectiveKeepDto toModify = ProspectiveKeepDtoTestFactory.createWithKey(key);
        toModify.setContent("test");
        final ServiceResponse modifyResult = prospectiveKeepService.modify(toModify);
        final ProspectiveKeepDto modified = (ProspectiveKeepDto) modifyResult.getRsltObj();

        // Then::
        assertNotNull(modified.getId(), "Modify did not keep key.");
        assertEquals("test", modified.getContent(), "Modify did not update content.");
    }

    /**
     * Delete keep.
     */
    @Test
    void delete() throws Exception {
        // Given::
        final ServiceResponse registResult = prospectiveKeepService.regist(prospectiveKeep);
        final ProspectiveKeepDto registered = (ProspectiveKeepDto) registResult.getRsltObj();
        final Integer key = registered.getKey();

        // When::
        final ServiceResponse deleteResult = prospectiveKeepService.delete(key);
        final Boolean isDeleted = deleteResult.getRslt();

        // Then::
        assertTrue(isDeleted, "Delete result was false.");
        assertThrows(EntityNotFoundException.class,
                () -> prospectiveKeepService.getDtlDto(key),
                "Deleted entity was still retrievable.");
    }

    /** Keep은 Project를 선택하거나 다시 미지정 상태로 돌릴 수 있다. */
    @Test
    void selectAndClearProject() throws Exception {
        final ProspectiveProjectDto project = registProject("가상 프로젝트");
        prospectiveKeep.setProjectId(project.getId());

        final ProspectiveKeepDto registered = (ProspectiveKeepDto) prospectiveKeepService.regist(prospectiveKeep).getRsltObj();
        assertEquals(project.getId(), registered.getProjectId());
        assertEquals(project.getId(), prospectiveKeepService.getDetailDtoWithCacheByUser(
                TestConstant.TEST_AUDITOR, registered.getId()).getProjectId());

        final ProspectiveKeepDto toModify = ProspectiveKeepDtoTestFactory.createWithKey(registered.getId());
        toModify.setProjectId(null);
        final ProspectiveKeepDto modified = (ProspectiveKeepDto) prospectiveKeepService.modify(toModify).getRsltObj();
        assertNull(modified.getProjectId());
    }

    /** Project 이동은 Keep 본문과 라이프사이클 데이터의 수정 경로를 거치지 않고 소속만 바꾼다. */
    @Test
    void moveProjectOnly() throws Exception {
        final ProspectiveProjectDto first = registProject("첫 프로젝트");
        final ProspectiveProjectDto second = registProject("둘째 프로젝트");
        prospectiveKeep.setProjectId(first.getId());
        prospectiveKeep.setContent("보존할 가상 내용");
        final ProspectiveKeepDto registered = (ProspectiveKeepDto) prospectiveKeepService.regist(prospectiveKeep).getRsltObj();

        prospectiveKeepService.moveProject(registered.getId(), second.getId());

        final ProspectiveKeepDto moved = prospectiveKeepService.getDtlDto(registered.getId());
        assertEquals(second.getId(), moved.getProjectId());
        assertEquals("보존할 가상 내용", moved.getContent());

        prospectiveKeepService.moveProject(registered.getId(), null);
        assertNull(prospectiveKeepService.getDtlDto(registered.getId()).getProjectId());
    }

    /** Project 삭제는 연결된 Keep을 보존하고 Project 미지정 상태로 되돌린다. */
    @Test
    void projectDeleteClearsKeepLink() throws Exception {
        final ProspectiveProjectDto project = registProject("삭제할 프로젝트");
        prospectiveKeep.setProjectId(project.getId());
        final ProspectiveKeepDto registered = (ProspectiveKeepDto) prospectiveKeepService.regist(prospectiveKeep).getRsltObj();

        prospectiveProjectService.delete(project.getId());

        final ProspectiveKeepDto retrieved = prospectiveKeepService.getDetailDtoWithCacheByUser(
                TestConstant.TEST_AUDITOR, registered.getId());
        assertNotNull(retrieved.getId());
        assertNull(retrieved.getProjectId());
    }

    /** 목록은 특정 Project와 Project 미지정 범위를 서로 분리한다. */
    @Test
    void filterByProjectScope() throws Exception {
        final ProspectiveProjectDto project = registProject("필터 프로젝트");
        prospectiveKeep.setProjectId(project.getId());
        final ProspectiveKeepDto linked = (ProspectiveKeepDto) prospectiveKeepService.regist(prospectiveKeep).getRsltObj();
        final ProspectiveKeepDto unassigned = (ProspectiveKeepDto) prospectiveKeepService.regist(
                ProspectiveKeepDto.builder().title("미지정 Keep").content("가상 내용").build()).getRsltObj();

        final ProspectiveKeepSearchParam projectSearch = ProspectiveKeepSearchParam.builder()
                .projectId(project.getId()).build();
        final ProspectiveKeepSearchParam unassignedSearch = ProspectiveKeepSearchParam.builder()
                .projectUnassigned(true).build();

        assertEquals(java.util.List.of(linked.getId()), prospectiveKeepService.getListDtoWithLifecycleByUser(
                TestConstant.TEST_AUDITOR, projectSearch).stream().map(ProspectiveKeepDto::getId).toList());
        assertTrue(prospectiveKeepService.getListDtoWithLifecycleByUser(
                TestConstant.TEST_AUDITOR, unassignedSearch).stream().map(ProspectiveKeepDto::getId).toList()
                .contains(unassigned.getId()));
    }

    /** 다른 사용자가 소유한 Project는 Keep에 연결할 수 없다. */
    @Test
    void rejectsForeignProject() {
        final ProspectiveProjectEntity foreignProject = prospectiveProjectRepository.saveAndFlush(
                ProspectiveProjectEntity.builder()
                        .title("다른 사용자 프로젝트")
                        .sortOrder(1)
                        .createdBy("fixture-other-user")
                        .build());
        prospectiveKeep.setProjectId(foreignProject.getId());

        assertThrows(io.nicheblog.dreamdiary.auth.security.exception.NotAuthorizedException.class,
                () -> prospectiveKeepService.regist(prospectiveKeep));
    }

    private ProspectiveProjectDto registProject(final String title) throws Exception {
        final ServiceResponse response = prospectiveProjectService.regist(
                ProspectiveProjectDto.builder().title(title).description("가상 설명").build());
        return (ProspectiveProjectDto) response.getRsltObj();
    }
}
