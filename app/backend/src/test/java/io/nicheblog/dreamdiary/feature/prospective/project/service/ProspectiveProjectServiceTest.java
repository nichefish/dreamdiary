package io.nicheblog.dreamdiary.feature.prospective.project.service;

import io.nicheblog.dreamdiary.auth.security.config.TestAuditConfig;
import io.nicheblog.dreamdiary.auth.security.exception.NotAuthorizedException;
import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.LifecycleKey;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.model.LifecycleSetDto;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.service.LifecycleService;
import io.nicheblog.dreamdiary.feature.attachable.state.repository.jpa.StateRepository;
import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.feature.prospective.project.model.ProspectiveProjectDto;
import io.nicheblog.dreamdiary.feature.prospective.project.repository.jpa.ProspectiveProjectRepository;
import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Prospective Project 사용자 소유 CRUD·정렬 계약 통합 테스트. */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestAuditConfig.class)
@Transactional
@WithMockUser(username = TestConstant.TEST_AUDITOR)
class ProspectiveProjectServiceTest {

    @Resource
    private ProspectiveProjectService prospectiveProjectService;
    @Resource
    private ProspectiveProjectRepository prospectiveProjectRepository;
    @Resource
    private LifecycleService lifecycleService;
    @Resource
    private StateRepository stateRepository;

    /** 등록은 사용자 목록 끝 순번을 부여하고 목록은 그 순서를 유지한다. */
    @Test
    void registAndList() throws Exception {
        final ProspectiveProjectDto first = regist("첫 프로젝트");
        final ProspectiveProjectDto second = regist("두 번째 프로젝트");

        assertEquals(1, first.getSortOrder());
        assertEquals(2, second.getSortOrder());
        assertEquals(List.of(first.getId(), second.getId()),
                prospectiveProjectService.getMyList().stream().map(ProspectiveProjectDto::getId).toList());
    }

    /** 수정 요청의 sortOrder는 무시하고 별도 정렬 API의 책임을 보존한다. */
    @Test
    void modifyKeepsSortOrder() throws Exception {
        final ProspectiveProjectDto registered = regist("수정 전");
        registered.setTitle("수정 후");
        registered.setSortOrder(99);

        final ProspectiveProjectDto modified = (ProspectiveProjectDto) prospectiveProjectService.modify(registered).getRsltObj();

        assertEquals("수정 후", modified.getTitle());
        assertEquals(1, modified.getSortOrder());
    }

    /** 정렬 요청은 사용자의 soft-delete되지 않은 Project 전체 순서를 1..N으로 다시 부여한다. */
    @Test
    void reorderRequiresCompleteOwnedSet() throws Exception {
        final ProspectiveProjectDto first = regist("첫 프로젝트");
        final ProspectiveProjectDto second = regist("두 번째 프로젝트");

        prospectiveProjectService.reorderMyProjects(List.of(second.getId(), first.getId()));

        assertEquals(List.of(second.getId(), first.getId()),
                prospectiveProjectService.getMyList().stream().map(ProspectiveProjectDto::getId).toList());
        assertThrows(IllegalArgumentException.class,
                () -> prospectiveProjectService.reorderMyProjects(List.of(first.getId())));
    }

    /** Project 목록은 저장 행이 없는 OPEN과 명시적으로 저장한 lifecycle을 함께 반환한다. */
    @Test
    void listIncludesProjectLifecycle() throws Exception {
        final ProspectiveProjectDto open = regist("진행 프로젝트");
        final ProspectiveProjectDto pending = regist("보류 프로젝트");
        lifecycleService.set(LifecycleSetDto.builder()
                .id(pending.getId())
                .contentType(ContentType.PROSPECTIVE_PROJECT)
                .lifecycleKey(LifecycleKey.PENDING)
                .build());

        final List<ProspectiveProjectDto> projects = prospectiveProjectService.getMyList();

        assertEquals(LifecycleKey.OPEN.key, projects.stream()
                .filter(project -> project.getId().equals(open.getId()))
                .findFirst().orElseThrow().getLifecycle().getLifecycleKey());
        assertEquals(LifecycleKey.PENDING.key, projects.stream()
                .filter(project -> project.getId().equals(pending.getId()))
                .findFirst().orElseThrow().getLifecycle().getLifecycleKey());
    }

    /** Project 완료는 lifecycle만 저장하고 화면 접힘 state를 파생하지 않는다. */
    @Test
    void resolvedProjectDoesNotCreateCollapsedState() throws Exception {
        final ProspectiveProjectDto project = regist("완료 프로젝트");

        lifecycleService.set(LifecycleSetDto.builder()
                .id(project.getId())
                .contentType(ContentType.PROSPECTIVE_PROJECT)
                .lifecycleKey(LifecycleKey.RESOLVED)
                .build());

        assertNull(stateRepository.findByRefIdAndRefContentTypeAndStateKey(
                project.getId(),
                ContentType.PROSPECTIVE_PROJECT.key,
                "COLLAPSED"
        ));
    }

    /** 삭제는 soft-delete되어 일반 상세 조회에서 제외된다. */
    @Test
    void delete() throws Exception {
        final ProspectiveProjectDto registered = regist("삭제 대상");

        assertTrue(prospectiveProjectService.delete(registered.getId()).getRslt());
        assertThrows(EntityNotFoundException.class,
                () -> prospectiveProjectService.getMyDetail(registered.getId()));
    }

    /** 다른 사용자가 소유한 Project의 상세·수정·삭제를 거부한다. */
    @Test
    void rejectsForeignOwner() {
        final ProspectiveProjectEntity foreignProject = prospectiveProjectRepository.saveAndFlush(
                ProspectiveProjectEntity.builder()
                        .title("다른 사용자 프로젝트")
                        .description("가상 설명")
                        .sortOrder(1)
                        .createdBy("fixture-other-user")
                        .build()
        );

        assertThrows(NotAuthorizedException.class,
                () -> prospectiveProjectService.getMyDetail(foreignProject.getId()));
        assertThrows(NotAuthorizedException.class,
                () -> prospectiveProjectService.modify(ProspectiveProjectDto.builder()
                        .id(foreignProject.getId())
                        .title("변경 시도")
                        .build()));
        assertThrows(NotAuthorizedException.class,
                () -> prospectiveProjectService.delete(foreignProject.getId()));
    }

    private ProspectiveProjectDto regist(final String title) throws Exception {
        final ProspectiveProjectDto dto = ProspectiveProjectDto.builder()
                .title(title)
                .description("가상 프로젝트 설명")
                .build();
        final ServiceResponse response = prospectiveProjectService.regist(dto);
        return (ProspectiveProjectDto) response.getRsltObj();
    }
}
