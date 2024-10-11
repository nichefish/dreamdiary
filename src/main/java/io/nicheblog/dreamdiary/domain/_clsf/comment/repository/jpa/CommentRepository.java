package io.nicheblog.dreamdiary.domain._clsf.comment.repository.jpa;

import io.nicheblog.dreamdiary.domain._clsf.comment.entity.CommentEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CommentRepository
 * <pre>
 *  댓글 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("commentRepository")
public interface CommentRepository
        extends BaseStreamRepository<CommentEntity, Integer> {

    /**
     * 댓글 엔티티(CommentEntity) 조회 :: entityGraph 사용 위해 override
     *
     * @param key 조회할 댓글 엔티티의 식별자 (키 값)
     * @return {@link Optional} -- 지정된 키 값에 해당하는 댓글 엔티티를 포함한 Optional 객체
     */
    @Override
    @EntityGraph(value = "CommentEntity.withCtgrCd")
    Optional<CommentEntity> findById(final @NotNull Integer key);
}