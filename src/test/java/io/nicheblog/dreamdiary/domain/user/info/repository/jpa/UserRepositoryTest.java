package io.nicheblog.dreamdiary.domain.user.info.repository.jpa;

import io.nicheblog.dreamdiary.domain.user.emplym.entity.UserEmplymEntity;
import io.nicheblog.dreamdiary.domain.user.emplym.entity.UserEmplymEntityTestFactory;
import io.nicheblog.dreamdiary.domain.user.info.entity.UserEntity;
import io.nicheblog.dreamdiary.domain.user.info.entity.UserEntityTestFactory;
import io.nicheblog.dreamdiary.domain.user.profl.entity.UserProflEntity;
import io.nicheblog.dreamdiary.domain.user.profl.entity.UserProflEntityTestFactory;
import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.config.DataSourceConfig;
import io.nicheblog.dreamdiary.global.config.TestAuditConfig;
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
 * UserRepositoryTest
 * <pre>
 *  UserRepository 테스트 모듈
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
class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private UserRepository userRepository;

    /**
     * regist 테스트
     */
    @Test
    public void testRegist() throws Exception {
        // Given::
        UserEntity user = UserEntityTestFactory.create();

        // When::
        Integer rsltId = userRepository.save(user).getUserNo();
        UserEntity rslt = userRepository.findById(rsltId).orElse(null);

        // Then::
        assertNotNull(rslt);
        assertNotNull(rslt.getUserNo());
        // audit
        assertNotNull(rslt.getRegDt());
        assertNotNull(rslt.getRegstrId());
        assertEquals(rslt.getRegstrId(), TestConstant.TEST_AUDITOR);
        // 항목 체크
        assertEquals(rslt.getUserId(), user.getUserId());
    }

    @Test
    public void testRegist_proflCascade() throws Exception {
        // Given::
        UserProflEntity userProfl = UserProflEntityTestFactory.create();
        UserEntity user = UserEntityTestFactory.create(userProfl);
        // cascade
        user.cascade();

        // When::
        Integer rsltId = userRepository.save(user).getUserNo();
        UserEntity rslt = userRepository.findById(rsltId).orElse(null);

        // Then::
        assertNotNull(rslt);
        assertNotNull(rslt.getProfl());
        assertNotNull(rslt.getProfl().getUserProflNo());
        // cascade 체크
        assertNotNull(rslt.getProfl().getUser());
        assertEquals(rslt.getProfl().getUser().getUserNo(), rsltId);
        // 항목 체크
        assertEquals(rslt.getProfl().getBrthdy(), userProfl.getBrthdy());
    }

    @Test
    public void testRegist_emplymCascade() throws Exception {
        // Given::
        UserEmplymEntity userEmplym = UserEmplymEntityTestFactory.create();
        UserEntity user = UserEntityTestFactory.create(userEmplym);

        // When::
        Integer rsltId = userRepository.save(user).getUserNo();
        UserEntity rslt = userRepository.findById(rsltId).orElse(null);

        // Then::
        assertNotNull(rslt);
        assertNotNull(rslt.getEmplym());
        assertNotNull(rslt.getEmplym().getUserEmplymNo());
        // cascade 체크
        assertNotNull(rslt.getEmplym().getUser());
        assertEquals(rslt.getProfl().getUser().getUserNo(), rsltId);
        // 항목 체크
        // 날짜 변환 체크
        assertEquals(rslt.getEmplym().getEcnyDt(),  userEmplym.getEcnyDt());
        assertEquals(rslt.getEmplym().getRetireDt(), userEmplym.getRetireDt());
        // 이메일 변환 로직
        assertEquals(rslt.getEmplym().getEmplymEmail(), userEmplym.getEmplymEmail());
    }
}