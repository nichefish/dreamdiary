package io.nicheblog.dreamdiary.web.repository.cmm.comment;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import org.springframework.stereotype.Repository;

/**
 * CommentRepository
 * <pre>
 *  게시판 댓글 Repository 인터페이스
 *  ※게시판 댓글(board_comment) = 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 */
@Repository("commentRepository")
public interface CommentRepository
        extends BaseStreamRepository<CommentEntity, Integer> {
    //
}