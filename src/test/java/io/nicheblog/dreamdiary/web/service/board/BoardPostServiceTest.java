/*
package io.nicheblog.dreamdiary.web.service.board;

import io.nicheblog.dreamdiary.web.entity.board.BoardPostEntity;
import io.nicheblog.dreamdiary.web.model.board.BoardPostDto;
import io.nicheblog.dreamdiary.web.model.board.BoardPostKey;
import io.nicheblog.dreamdiary.web.repository.board.BoardPostRepository;
import io.nicheblog.dreamdiary.web.spec.board.BoardPostSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BoardPostServiceTest {

    @Mock
    private BoardPostRepository boardPostRepository;

    @Mock
    private BoardPostSpec boardPostSpec;

    @InjectMocks
    private BoardPostService boardPostService;

    private BoardPostEntity sampleEntity;
    private BoardPostDto sampleDto;

    @BeforeEach


    @Test
    void testCreatePost() throws Exception {
        */
/* 준비arrange = 테스트에 필요한 조건 및 데이터 준비 *//*

        // 헬퍼 메소드 호출 (mock객체 또는 구체적인 인스턴스)
        BoardPostDto samplePostDto = this.createSampleData();


        */
/* 실행조건when: 모킹 프레임워크 사용시 처리하는 단계. (준비arrange의 일부) *//*

        // "you can mock the service's response here."
        when(boardPostRepository.save(any(BoardPostEntity.class)))
            .thenReturn(sampleEntity);

        BoardPostDto result = boardPostService.getDtlDto(new BoardPostKey(1, "test"));
        assertNotNull(result);
        assertEquals(sampleDto.getTitle(), result.getTitle());
    }

    @Test
    void testReadPost() throws Exception {
        when(boardPostRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(sampleEntity));

        BoardPostDto result = boardPostService.getDtlDto(new BoardPostKey(1L));
        assertNotNull(result);
        assertEquals("Sample Title", result.getTitle());
    }

    @Test
    void testUpdatePost() throws Exception {
        when(boardPostRepository.save(any(BoardPostEntity.class))).thenReturn(sampleEntity);
        sampleEntity.setTitle("Updated Title");
        sampleDto.setTitle("Updated Title");

        BoardPostDto updated = boardPostService.getDtlDto(new BoardPostKey(1L));
        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
    }

    @Test
    void testDeletePost() {
        // 예시: 삭제 테스트는 실제 삭제 로직을 실행하고, 성공적으로 처리되었는지 확인합니다.
        // 이 부분은 상황에 따라 다르게 구현될 수 있으므로, 구체적인 구현은 생략합니다.
    }

    */
/**
     * 샘플 데이터 생성
     *//*

    BoardPostDto createSampleData() {
        // 샘플 데이터 초기화
        sampleDto = new BoardPostDto();
        sampleDto.setId(1L);
        sampleDto.setTitle("Sample Title");
        sampleDto.setContent("Sample content...");
        return sampleDto;
    }
}*/
