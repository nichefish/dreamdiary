package io.nicheblog.dreamdiary.domain.jrnl.day.repository.jpa;

import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDayEntity;
import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDayEntityTestFactory;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryEntityTestFactory;
import io.nicheblog.dreamdiary.domain.jrnl.diary.repository.jpa.JrnlDiaryRepository;
import io.nicheblog.dreamdiary.domain.jrnl.dream.entity.JrnlDreamEntity;
import io.nicheblog.dreamdiary.domain.jrnl.dream.entity.JrnlDreamEntityTestFactory;
import io.nicheblog.dreamdiary.domain.jrnl.dream.repository.jpa.JrnlDreamRepository;
import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.config.DataSourceConfig;
import io.nicheblog.dreamdiary.global.config.TestAuditConfig;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * JrnlDayRepositoryTest
 * <pre>
 *  JrnlDayRepository 테스트 모듈
 *  "@Transactional 환경에서는 flush가 의도한 대로 작동하지 않을 수 있다."
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
@Transactional
class JrnlDayRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JrnlDayRepository jrnlDayRepository;
    @Autowired
    private JrnlDreamRepository jrnlDreamRepository;
    @Autowired
    private JrnlDiaryRepository jrnlDiaryRepository;

    private JrnlDayEntity jrnlDayEntity;

    /**
     * 각 테스트 시작 전 세팅 초기화.
     * @throws Exception 발생할 수 있는 예외.
     */
    @BeforeEach
    void setUp() throws Exception {
        // 공통적으로 사용할 JrnlDayDto 초기화
        jrnlDayEntity = JrnlDayEntityTestFactory.createWithJrnlDt("2000-01-01");
    }

    /**
     * regist 테스트
     */
    @Test
    public void testRegist() throws Exception {
        // Given::
        JrnlDayEntity saved = jrnlDayRepository.save(jrnlDayEntity);
        Integer rsltId = saved.getPostNo();

        // When::
        JrnlDayEntity rslt = jrnlDayRepository.findById(rsltId).orElse(null);

        // Then::
        assertNotNull(rslt, "저장한 데이터를 조회할 수 없습니다.");
        assertNotNull(rslt.getPostNo(), "저장된 엔티티의 key 값이 없습니다.");
        // audit
        assertNotNull(rslt.getRegDt(), "등록일자 audit 처리가 되지 않았습니다.");
        assertNotNull(rslt.getRegstrId(),  "등록자 audit 처리가 되지 않았습니다.");
        assertEquals(rslt.getRegstrId(), TestConstant.TEST_AUDITOR, "등록자가 예상 값과 일치하지 않습니다.");
    }

    /**
     * 해당 날짜 중복 체크(countByJrnlDt) 테스트
     */
    @Test
    public void testCountByJrnlDt() throws Exception {
        // Given::
        jrnlDayRepository.save(jrnlDayEntity);

        // When::
        Integer count = jrnlDayRepository.countByJrnlDt(DateUtils.asDate("2000-01-01"));

        // Then::
        assertNotNull(count, "중복 체크 메소드가 제대로 실행되지 않았습니다.");
        assertTrue(count >= 1, "해당 날짜에 대한 중복 체크가 실패하였습니다. 저장된 데이터가 1개 이상이어야 합니다.");
    }

    /**
     * 날짜로 저널 일자 조회 테스트
     */
    @Test
    public void testFindByJrnlDt() throws Exception {
        // Given::
        JrnlDayEntity result = jrnlDayRepository.save(jrnlDayEntity);

        // When::
        JrnlDayEntity retrieved = jrnlDayRepository.findByJrnlDt(DateUtils.asDate("2000-01-01"));

        // Then::
        assertNotNull(retrieved, "메소드가 제대로 실행되지 않았습니다.");
        assertEquals(result.getPostNo(), retrieved.getPostNo(), "날짜를 이용한 조회에 실패했습니다.");
    }


    /**
     * jrnlDream subentity select 테스트
     * 1. 메인엔티티 등록, 2. 서브엔티티 등록 후 3. 메인엔티티 재조회
     */
    @Test
    public void testGetDreamList() throws Exception {
        // Given::
        JrnlDayEntity registered = jrnlDayRepository.save(jrnlDayEntity);
        Integer jrnlDayNo = registered.getPostNo();

        testEntityManager.clear();

        // When::
        // 저널 꿈 regist
        JrnlDreamEntity jrnlDream = JrnlDreamEntityTestFactory.create();
        jrnlDream.setJrnlDayNo(jrnlDayNo);
        jrnlDreamRepository.save(jrnlDream);

        JrnlDayEntity retrieved = jrnlDayRepository.findById(jrnlDayNo).orElseThrow(() -> new EntityNotFoundException("저널 일자를 찾을 수 없습니다."));
        List<JrnlDreamEntity> dreamList = retrieved.getJrnlDreamList();

        // Then::
        assertNotNull(retrieved);
        assertNotNull(jrnlDayNo);
        // jrnlDream
        assertNotNull(retrieved.getJrnlDreamList());
    }

    /**
     * jrnlDiary subentity select 테스트
     * 1. 메인엔티티 등록, 2. 서브엔티티 등록 후 3. 메인엔티티 재조회
     */
    @Test
    public void testGetDiaryList() throws Exception {
        // Given::
        JrnlDayEntity registered = jrnlDayRepository.save(jrnlDayEntity);
        Integer jrnlDayNo = registered.getPostNo();

        testEntityManager.clear();

        // When::
        // 저널 꿈 regist
        JrnlDiaryEntity jrnlDiary = JrnlDiaryEntityTestFactory.create();
        jrnlDiary.setJrnlDayNo(jrnlDayNo);
        jrnlDiaryRepository.save(jrnlDiary);

        JrnlDayEntity retrieved = jrnlDayRepository.findById(jrnlDayNo).orElseThrow(() -> new EntityNotFoundException("저널 일자를 찾을 수 없습니다."));
        List<JrnlDreamEntity> dreamList = retrieved.getJrnlDreamList();

        // Then::
        assertNotNull(retrieved);
        assertNotNull(jrnlDayNo);
        // jrnlDiary
        assertNotNull(retrieved.getJrnlDiaryList());
    }
}