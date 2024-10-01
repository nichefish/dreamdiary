package io.nicheblog.dreamdiary.web.repository.cmm.comment.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CommentRepository
 * <pre>
 *  게시판 댓글 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("commentRepository")
public interface CommentRepository
        extends BaseStreamRepository<CommentEntity, Integer> {

    @Override
    @EntityGraph(value = "CommentEntity.withCtgrCd")
    Optional<CommentEntity> findById(final Integer key);
}