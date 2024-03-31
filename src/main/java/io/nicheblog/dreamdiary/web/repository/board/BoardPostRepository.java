package io.nicheblog.dreamdiary.web.repository.board;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.board.BoardPostEntity;
import org.springframework.stereotype.Repository;

/**
 * BoardPostRepository
 * <pre>
 *  일반게시판 게시물 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("boardPostRepository")
public interface BoardPostRepository
        extends BaseRepository<BoardPostEntity, BaseClsfKey> {
    //
}
