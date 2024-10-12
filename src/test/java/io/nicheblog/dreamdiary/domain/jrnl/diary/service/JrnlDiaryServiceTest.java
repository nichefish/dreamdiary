package io.nicheblog.dreamdiary.domain.jrnl.diary.service;

import io.nicheblog.dreamdiary.domain.jrnl.diary.model.JrnlDiaryDto;
import io.nicheblog.dreamdiary.domain.jrnl.diary.model.JrnlDiaryDtoTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JrnlDiaryServiceTest
 * <pre>
 *  저널 일기 서비스 테스트 모듈
 *  "@Transactional 어노테이션 적용시 테스트 이후 트랜잭션이 롤백된다."
 * </pre>
 * 
 * @author nichefish 
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JrnlDiaryServiceTest {
    
    @Resource
    private JrnlDiaryService jrnlDiaryService;

    private JrnlDiaryDto jrnlDiary;

    /**
     * 각 테스트 시작 전 세팅 초기화.
     * @throws Exception 발생할 수 있는 예외.
     */
    @BeforeEach
    void setUp() throws Exception {
        // 공통적으로 사용할 JrnlDiaryDto 초기화
        jrnlDiary = JrnlDiaryDtoTestFactory.create();
    }

    /**
     * 저널 일기 등록
     * @throws Exception 등록 중 발생할 수 있는 예외
     */
    @Test
    void regist() throws Exception {
        // Given::

        // When::
        JrnlDiaryDto result = jrnlDiaryService.regist(jrnlDiary);

        // Then::
        assertNotNull(result.getPostNo(), "등록이 정상적으로 이루어지지 않았습니다.");
    }

    /**
     * 저널 일기 수정
     * @throws Exception 등록 중 발생할 수 있는 예외
     */
    @Test
    void modify() throws Exception {
        // Given::
        JrnlDiaryDto base = jrnlDiaryService.regist(jrnlDiary);
        Integer key = base.getKey();

        // When::
        JrnlDiaryDto toModify = JrnlDiaryDtoTestFactory.createWithKey(key);
        toModify.setCn("test");
        JrnlDiaryDto result = jrnlDiaryService.modify(toModify);

        // Then::
        assertNotNull(result.getPostNo(), "수정이 정상적으로 이루어지지 않았습니다.");
        assertEquals("test", result.getCn(), "수정이 정상적으로 이루어지지 않았습니다.");
    }

    /**
     * 저널 일기 삭제
     * @throws Exception 등록 중 발생할 수 있는 예외
     */
    @Test
    void delete() throws Exception {
        // Given::
        JrnlDiaryDto base = jrnlDiaryService.regist(jrnlDiary);
        Integer key = base.getKey();

        // When::
        Boolean isDeleted = jrnlDiaryService.delete(key);

        // Then::
        assertTrue(isDeleted, "삭제가 정상적으로 이루어지지 않았습니다.");
        // 삭제된 엔티티 조회
        assertThrows(EntityNotFoundException.class,
                () -> jrnlDiaryService.getDtlDto(key),
                "삭제된 엔티티를 조회하려고 했으나 예외가 발생하지 않았습니다."
        );
    }
}