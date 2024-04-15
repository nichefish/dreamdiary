package io.nicheblog.dreamdiary.web.repository.dream.piece;

import io.nicheblog.dreamdiary.global.config.DataSourceConfig;
import io.nicheblog.dreamdiary.global.config.TestConfig;
import io.nicheblog.dreamdiary.web.entity.dream.DreamPieceEntity;
import io.nicheblog.dreamdiary.web.entity.dream.piece.DreamPieceEntityTestFactory;
import io.nicheblog.dreamdiary.web.repository.dream.DreamPieceRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * DreamPieceRepositoryTest
 * <pre>
 *  DreamPieceRepository 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestEntityManager
@ImportAutoConfiguration(DataSourceConfig.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
@Log4j2
class DreamPieceRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private DreamPieceRepository dreamPieceRepository;

    /**
     * regist 테스트
     */
    @Test
    public void testRegist() throws Exception {
        // Given::
        DreamPieceEntity dreamPiece = DreamPieceEntityTestFactory.createDreamPiece();

        // When::
        Integer rsltId = dreamPieceRepository.save(dreamPiece).getPostNo();
        DreamPieceEntity rslt = dreamPieceRepository.findById(rsltId).orElse(null);

        // Then::
        assertNotNull(rslt);
        assertNotNull(rslt.getPostNo());
        // audit
        assertNotNull(rslt.getRegDt());
        assertNotNull(rslt.getRegstrId());
        assertEquals(rslt.getRegstrId(), "test_user");
    }
}