package io.nicheblog.dreamdiary.web.repository.board.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.board.BoardDefEntity;
import org.springframework.stereotype.Repository;

/**
 * BoardDefRepository
 * <pre>
 *  게시판 정의 정보 repository 인터페이스
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Repository("boardDefRepository")
public interface BoardDefRepository
        extends BaseStreamRepository<BoardDefEntity, String> {
    //
}

