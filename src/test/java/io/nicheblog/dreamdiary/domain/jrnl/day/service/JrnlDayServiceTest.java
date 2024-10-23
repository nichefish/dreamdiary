package io.nicheblog.dreamdiary.domain.jrnl.day.service;

import io.nicheblog.dreamdiary.domain.jrnl.day.model.JrnlDayDto;
import io.nicheblog.dreamdiary.domain.jrnl.day.model.JrnlDayDtoTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JrnlDayServiceTest
 *
 * @author nichefish
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JrnlDayServiceTest {

    @Resource
    private JrnlDayService jrnlDayService;

    /**
     * 등록
     * @throws Exception 등록 중 발생할 수 있는 예외
     */
    @Test
    void regist() throws Exception {
        // Given::
        JrnlDayDto jrnlDay = JrnlDayDtoTestFactory.createWithJrnlDt("2000-01-01");

        // When::
        JrnlDayDto result = jrnlDayService.regist(jrnlDay);

        // Then::
        assertNotNull(result.getPostNo());
    }

    /**
     * 수정
     * @throws Exception 등록 중 발생할 수 있는 예외
     */
    @Test
    @Transactional
    void modify() {
    }

    /**
     * 삭제
     * @throws Exception 등록 중 발생할 수 있는 예외
     */
    @Test
    @Transactional
    void delete() {
    }
}