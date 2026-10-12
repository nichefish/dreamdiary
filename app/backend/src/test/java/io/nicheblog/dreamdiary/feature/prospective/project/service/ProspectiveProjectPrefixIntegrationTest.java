package io.nicheblog.dreamdiary.feature.prospective.project.service;

import io.nicheblog.dreamdiary.auth.security.config.TestAuditConfig;
import io.nicheblog.dreamdiary.auth.security.exception.NotAuthorizedException;
import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.prefix.model.PrefixDto;
import io.nicheblog.dreamdiary.feature.attachable.prefix.repository.jpa.PrefixContentRepository;
import io.nicheblog.dreamdiary.feature.attachable.prefix.service.PrefixService;
import io.nicheblog.dreamdiary.feature.prospective.project.model.ProspectiveProjectDto;
import io.nicheblog.dreamdiary.feature.user.account.entity.UserEntity;
import io.nicheblog.dreamdiary.feature.user.account.entity.UserEntityTestFactory;
import io.nicheblog.dreamdiary.feature.user.account.repository.jpa.UserRepository;
import io.nicheblog.dreamdiary.global.TestConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Project 개인 말머리(PERSONAL + PROSPECTIVE_PROJECT Scope, prefix_content 연결) 선택·표시 계약 통합 테스트.
 *
 * @author nichefish
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestAuditConfig.class)
@Transactional
@WithMockUser(username = TestConstant.TEST_AUDITOR)
class ProspectiveProjectPrefixIntegrationTest {

    private static final String FIXTURE_OTHER_USERNAME = "project_prefix_other";
    private static final String PROJECT_TYPE = ContentType.PROSPECTIVE_PROJECT.key;

    @Resource
    private ProspectiveProjectService prospectiveProjectService;
    @Resource
    private PrefixService prefixService;
    @Resource
    private PrefixContentRepository prefixContentRepository;
    @Resource
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    /** 개인 Scope는 사용자 ID로 식별하므로 감사자 계정을 준비한다. */
    @BeforeEach
    void setUp() throws Exception {
        ensureUser(TestConstant.TEST_AUDITOR, "project-prefix-auditor@example.test");
    }

    /** 등록 시 선택한 말머리는 prefix_content(PROSPECTIVE_PROJECT)로 연결되고 응답·목록·상세에 표시된다. */
    @Test
    void registWithPrefixShowsInResponseListAndDetail() throws Exception {
        final PrefixDto game = createPrefix("게임", 1);

        final ProspectiveProjectDto registered = regist("왕국건설스토리", game.getId());
        flushAndClear();

        assertEquals(game.getId(), registered.getPrefixId());
        assertEquals("게임", registered.getPrefix().getName());
        assertTrue(prefixContentRepository.findByRefIdAndRefContentType(registered.getId(), PROJECT_TYPE).isPresent());

        final ProspectiveProjectDto listed = findInList(registered.getId());
        assertEquals(game.getId(), listed.getPrefixId());
        assertEquals("게임", listed.getPrefix().getName());
        assertEquals("Y", listed.getPrefix().getActiveYn());

        final ProspectiveProjectDto detail = prospectiveProjectService.getMyDetail(registered.getId());
        assertEquals(game.getId(), detail.getPrefix().getId());
    }

    /** 말머리 없는 Project는 prefix·prefixId 모두 null이다. */
    @Test
    void projectWithoutPrefixHasNullPrefix() throws Exception {
        final ProspectiveProjectDto registered = regist("말머리 없음", null);
        flushAndClear();

        final ProspectiveProjectDto listed = findInList(registered.getId());
        assertNull(listed.getPrefix());
        assertNull(listed.getPrefixId());
    }

    /** 수정은 말머리를 교체하고, prefixId null(빈 값)은 연결을 해제한다. */
    @Test
    void modifyChangesAndClearsPrefix() throws Exception {
        final PrefixDto game = createPrefix("게임", 1);
        final PrefixDto study = createPrefix("공부", 2);
        final ProspectiveProjectDto registered = regist("교체 대상", game.getId());

        final ProspectiveProjectDto changed = modify(registered.getId(), "교체 대상", study.getId());
        flushAndClear();
        assertEquals(study.getId(), changed.getPrefixId());
        assertEquals(study.getId(), findInList(registered.getId()).getPrefixId());

        final ProspectiveProjectDto cleared = modify(registered.getId(), "교체 대상", null);
        flushAndClear();
        assertNull(cleared.getPrefix());
        assertNull(findInList(registered.getId()).getPrefix());
        assertTrue(prefixContentRepository.findByRefIdAndRefContentType(registered.getId(), PROJECT_TYPE).isEmpty());
    }

    /** 비활성 말머리는 신규 선택을 거부하지만, 이미 붙은 Project는 유지·표시한다. */
    @Test
    void inactivePrefixRejectedForNewSelectionButKeptOnExisting() throws Exception {
        final PrefixDto game = createPrefix("게임", 1);
        final ProspectiveProjectDto existing = regist("기존 선택", game.getId());
        prefixService.setActive(PROJECT_TYPE, game.getId(), false);
        flushAndClear();

        assertThrows(IllegalStateException.class, () -> regist("신규 선택", game.getId()));

        final ProspectiveProjectDto kept = modify(existing.getId(), "기존 선택 유지", game.getId());
        assertEquals(game.getId(), kept.getPrefixId());
        flushAndClear();
        final ProspectiveProjectDto listed = findInList(existing.getId());
        assertEquals("N", listed.getPrefix().getActiveYn());
    }

    /** 다른 사용자의 말머리는 선택할 수 없다. */
    @Test
    void foreignPrefixRejected() throws Exception {
        createPrefix("내 말머리", 1);
        ensureUser(FIXTURE_OTHER_USERNAME, "project-prefix-other@example.test");
        final PrefixDto foreign = runAs(FIXTURE_OTHER_USERNAME, () -> createPrefix("남의 말머리", 1));
        flushAndClear();

        assertThrows(NotAuthorizedException.class, () -> regist("타인 말머리 시도", foreign.getId()));
    }

    /** Project 말머리 목록은 Keep 말머리(PROSPECTIVE_KEEP)와 독립된 Scope다. */
    @Test
    void keepPrefixCannotBeUsedForProject() throws Exception {
        createPrefix("Project용", 1);
        final PrefixDto keepPrefix = prefixService.create(ContentType.PROSPECTIVE_KEEP.key, PrefixDto.builder()
                .name("Keep용")
                .textClassCd("MUTED")
                .sortOrder(1)
                .build());
        flushAndClear();

        assertThrows(NotAuthorizedException.class, () -> regist("Keep 말머리 시도", keepPrefix.getId()));
    }

    private ProspectiveProjectDto regist(final String title, final Integer prefixId) throws Exception {
        return (ProspectiveProjectDto) prospectiveProjectService.regist(ProspectiveProjectDto.builder()
                .title(title)
                .description("가상 프로젝트 설명")
                .prefixId(prefixId)
                .build()).getRsltObj();
    }

    private ProspectiveProjectDto modify(final Integer id, final String title, final Integer prefixId) throws Exception {
        return (ProspectiveProjectDto) prospectiveProjectService.modify(ProspectiveProjectDto.builder()
                .id(id)
                .title(title)
                .description("가상 프로젝트 설명")
                .prefixId(prefixId)
                .build()).getRsltObj();
    }

    private ProspectiveProjectDto findInList(final Integer id) {
        final List<ProspectiveProjectDto> list = prospectiveProjectService.getMyList();
        return list.stream().filter(project -> project.getId().equals(id)).findFirst().orElseThrow();
    }

    private PrefixDto createPrefix(final String name, final Integer sortOrder) {
        return prefixService.create(PROJECT_TYPE, PrefixDto.builder()
                .name(name)
                .textClassCd("MUTED")
                .sortOrder(sortOrder)
                .build());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    /** 지정 가상 사용자 인증으로 작업하고 원래 인증을 복원한다. */
    private <T> T runAs(final String username, final Supplier<T> action) {
        final Authentication original = SecurityContextHolder.getContext().getAuthentication();
        final UserDetails principal = User.withUsername(username)
                .password("fixture-password")
                .roles("USER")
                .build();
        try {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
            return action.get();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(original);
        }
    }

    private void ensureUser(final String username, final String email) throws Exception {
        if (userRepository.findByUsername(username).isPresent()) return;
        final UserEntity user = UserEntityTestFactory.create();
        user.setUsername(username);
        user.setEmail(email);
        userRepository.saveAndFlush(user);
    }
}
