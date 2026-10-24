package io.nicheblog.dreamdiary.feature.prospective.keep.repository.jpa;

import io.nicheblog.dreamdiary.auth.security.config.TestAuditConfig;
import io.nicheblog.dreamdiary.feature.prospective.keep.entity.ProspectiveKeepEntity;
import io.nicheblog.dreamdiary.feature.prospective.keep.entity.ProspectiveKeepEntityTestFactory;
import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.feature.prospective.project.repository.jpa.ProspectiveProjectRepository;
import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.config.DataSourceConfig;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProspectiveKeepRepositoryTest
 * <pre>
 *  JPA repository tests for ProspectiveKeep.
 * </pre>
 *
 * @author nichefish
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(DataSourceConfig.class)
@Import(TestAuditConfig.class)
@Log4j2
class ProspectiveKeepRepositoryTest {

    @Resource
    private ProspectiveKeepRepository prospectiveKeepRepository;
    @Resource
    private ProspectiveProjectRepository prospectiveProjectRepository;

    private ProspectiveKeepEntity prospectiveKeepEntity;

    /**
     * Initialize test data before each case.
     */
    @BeforeEach
    void setUp() throws Exception {
        prospectiveKeepEntity = ProspectiveKeepEntityTestFactory.create();
    }

    /**
     * Persist entity.
     */
    @Test
    void testRegist() throws Exception {
        // Given::

        // When::
        final ProspectiveKeepEntity registered = prospectiveKeepRepository.save(prospectiveKeepEntity);
        final Integer key = registered.getId();
        final ProspectiveKeepEntity retrieved = prospectiveKeepRepository.findById(key)
                .orElseThrow(() -> new EntityNotFoundException(MessageUtils.getMessage("exception.EntityNotFoundException.registered")));

        // Then::
        assertNotNull(retrieved, "Could not load saved entity.");
        assertNotNull(retrieved.getId(), "Saved entity key is null.");
        assertNotNull(retrieved.getCreatedAt(), "createdAt audit was not set.");
        assertNotNull(retrieved.getCreatedBy(), "createdBy audit was not set.");
        assertEquals(TestConstant.TEST_AUDITOR, retrieved.getCreatedBy(), "Saved createdBy is unexpected.");
    }

    /**
     * Update entity.
     */
    @Test
    void testModify() throws Exception {
        // Given::
        final ProspectiveKeepEntity registered = prospectiveKeepRepository.save(prospectiveKeepEntity);
        final Integer key = registered.getId();

        // When::
        final ProspectiveKeepEntity toModify = prospectiveKeepRepository.findById(key)
                .orElseThrow(() -> new EntityNotFoundException(MessageUtils.getMessage("exception.EntityNotFoundException.to-modify")));
        toModify.setContent("modified");
        final ProspectiveKeepEntity modified = prospectiveKeepRepository.saveAndFlush(toModify);

        // Then::
        assertNotNull(modified, "Could not load modified entity.");
        assertNotNull(modified.getId(), "Modified entity key is null.");
        assertNotNull(modified.getUpdatedAt(), "updatedAt audit was not set.");
        assertNotNull(modified.getUpdatedBy(), "updatedBy audit was not set.");
        assertEquals(TestConstant.TEST_AUDITOR, modified.getUpdatedBy(), "Modified updatedBy is unexpected.");
        assertEquals("modified", modified.getContent(), "Content was not updated.");
    }

    /**
     * Delete entity.
     */
    @Test
    void testDelete() throws Exception {
        // Given::
        final ProspectiveKeepEntity registered = prospectiveKeepRepository.save(prospectiveKeepEntity);
        final Integer key = registered.getId();

        // When::
        final ProspectiveKeepEntity toDelete = prospectiveKeepRepository.findById(key)
                .orElseThrow(() -> new EntityNotFoundException(MessageUtils.getMessage("exception.EntityNotFoundException.to-delete")));
        prospectiveKeepRepository.delete(toDelete);
        final ProspectiveKeepEntity retrieved = prospectiveKeepRepository.findById(key).orElse(null);

        // Then::
        assertNull(retrieved, "Delete did not remove the entity.");
    }

    /** Project 연결 해제 쿼리는 Keep을 삭제하지 않고 nullable 관계만 비운다. */
    @Test
    void clearProjectLink() throws Exception {
        final ProspectiveProjectEntity project = prospectiveProjectRepository.saveAndFlush(
                ProspectiveProjectEntity.builder().title("가상 프로젝트").sortOrder(1).build());
        prospectiveKeepEntity.setProject(project);
        final ProspectiveKeepEntity registered = prospectiveKeepRepository.saveAndFlush(prospectiveKeepEntity);

        final int cleared = prospectiveKeepRepository.clearProjectByProjectIdAndCreatedBy(
                project.getId(), TestConstant.TEST_AUDITOR);

        assertEquals(1, cleared);
        assertNull(prospectiveKeepRepository.findById(registered.getId()).orElseThrow().getProject());
    }
}
