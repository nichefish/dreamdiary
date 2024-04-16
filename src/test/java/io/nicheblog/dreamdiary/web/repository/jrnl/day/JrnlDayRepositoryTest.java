package io.nicheblog.dreamdiary.web.repository.jrnl.day;

import io.nicheblog.dreamdiary.global.config.DataSourceConfig;
import io.nicheblog.dreamdiary.global.config.TestAuditConfig;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntityTestFactory;
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
 * JrnlDayRepositoryTest
 * <pre>
 *  JrnlDayRepository 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestEntityManager
@ImportAutoConfiguration(DataSourceConfig.class)
@Import(TestAuditConfig.class)
@ActiveProfiles("test")
@Log4j2
class JrnlDayRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private JrnlDayRepository jrnlDayRepository;

    /**
     * regist 테스트
     */
    @Test
    public void testRegist() throws Exception {
        // Given::
        JrnlDayEntity jrnlDay = JrnlDayEntityTestFactory.createJrnlDay();
        jrnlDay.setJrnlDt(DateUtils.asDate("2000-01-01"));

        // When::
        Integer rsltId = jrnlDayRepository.save(jrnlDay).getPostNo();
        JrnlDayEntity rslt = jrnlDayRepository.findById(rsltId).orElse(null);

        // Then::
        assertNotNull(rslt);
        assertNotNull(rslt.getPostNo());
        // audit
        assertNotNull(rslt.getRegDt());
        assertNotNull(rslt.getRegstrId());
        assertEquals(rslt.getRegstrId(), "test_user");
    }
}