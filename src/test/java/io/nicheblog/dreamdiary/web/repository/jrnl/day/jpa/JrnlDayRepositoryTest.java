package io.nicheblog.dreamdiary.web.repository.jrnl.day.jpa;

import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.config.DataSourceConfig;
import io.nicheblog.dreamdiary.global.config.TestAuditConfig;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntityTestFactory;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntityTestFactory;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntityTestFactory;
import io.nicheblog.dreamdiary.web.repository.jrnl.day.jpa.JrnlDayRepository;
import io.nicheblog.dreamdiary.web.repository.jrnl.diary.jpa.JrnlDiaryRepository;
import io.nicheblog.dreamdiary.web.repository.jrnl.dream.jpa.JrnlDreamRepository;
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

import javax.transaction.Transactional;

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
    @Autowired
    private JrnlDreamRepository jrnlDreamRepository;
    @Autowired
    private JrnlDiaryRepository jrnlDiaryRepository;

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
        assertEquals(rslt.getRegstrId(), TestConstant.TEST_AUDITOR);
    }

    /**
     * jrnlDream subentity select 테스트
     * 1. 메인엔티티 등록, 2. 서브엔티티 등록 후 3. 메인엔티티 재조회
     */
    @Test
    @Transactional
    public void testGetDreamList() throws Exception {
        // Given::
        JrnlDayEntity jrnlDay = JrnlDayEntityTestFactory.createJrnlDay();
        jrnlDay.setJrnlDt(DateUtils.asDate("2000-01-01"));
        JrnlDayEntity rslt = jrnlDayRepository.saveAndFlush(jrnlDay);
        Integer rsltId = rslt.getPostNo();

        // When::
        // 저널 꿈 regist
        JrnlDreamEntity jrnlDream = JrnlDreamEntityTestFactory.createJrnlDream();
        jrnlDream.setJrnlDayNo(rsltId);
        jrnlDreamRepository.saveAndFlush(jrnlDream);
        jrnlDayRepository.refresh(rslt);

        // Then::
        assertNotNull(rslt);
        assertNotNull(rsltId);
        // jrnlDream
        assertNotNull(rslt.getJrnlDreamList());
    }

    /**
     * jrnlDiary subentity select 테스트
     * 1. 메인엔티티 등록, 2. 서브엔티티 등록 후 3. 메인엔티티 재조회
     */
    @Test
    @Transactional
    public void testGetDiaryList() throws Exception {
        // Given::
        JrnlDayEntity jrnlDay = JrnlDayEntityTestFactory.createJrnlDay();
        jrnlDay.setJrnlDt(DateUtils.asDate("2000-01-01"));
        JrnlDayEntity rslt = jrnlDayRepository.saveAndFlush(jrnlDay);
        Integer rsltId = rslt.getPostNo();

        // When::
        // 저널 꿈 regist
        JrnlDiaryEntity jrnlDiary = JrnlDiaryEntityTestFactory.createJrnlDiary();
        jrnlDiary.setJrnlDayNo(rsltId);
        jrnlDiaryRepository.saveAndFlush(jrnlDiary);
        jrnlDayRepository.refresh(rslt);

        // Then::
        assertNotNull(rslt);
        assertNotNull(rsltId);
        // jrnlDiary
        assertNotNull(rslt.getJrnlDiaryList());
    }
}