package io.nicheblog.dreamdiary.web.repository.jrnl.day.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayTagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * JrnlDayTagRepository
 * <pre>
 *  저널 일자 태그 정보 repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */

@Repository("jrnlDayTagRepository")
public interface JrnlDayTagRepository
        extends BaseStreamRepository<JrnlDayTagEntity, Integer> {

    @Override
    @EntityGraph(value = "JrnlDayTagEntity.withTag")
    Optional<JrnlDayTagEntity> findById(final Integer key);

    @Override
    @EntityGraph(value = "JrnlDayTagEntity.withTag")
    Page<JrnlDayTagEntity> findAll(final @Nullable Specification<JrnlDayTagEntity> spec, final Pageable pageable);

    @Override
    @EntityGraph(value = "JrnlDayTagEntity.withTag")
    List<JrnlDayTagEntity> findAll(final @Nullable Specification<JrnlDayTagEntity> spec);

    @Override
    @EntityGraph(value = "JrnlDayTagEntity.withTag")
    List<JrnlDayTagEntity> findAll(final @Nullable Specification<JrnlDayTagEntity> spec, final Sort sort);

    @Override
    @EntityGraph(value = "JrnlDayTagEntity.withTag")
    Stream<JrnlDayTagEntity> streamAllBy(final @Nullable Specification<JrnlDayTagEntity> spec);

    /**
     * 년도/월별 저널 꿈 태그 개수 조회
     */
    @Query("SELECT COUNT(contentTag.contentTagNo) " +
            "FROM JrnlDayContentTagEntity contentTag " +
            "INNER JOIN FETCH JrnlDayEntity day ON contentTag.refPostNo = day.postNo " +
            "WHERE contentTag.refTagNo = :tagNo " +
            " AND (:yy IS NULL OR day.yy = :yy OR :yy = 9999) " +
            " AND (:mnth IS NULL OR day.mnth = :mnth OR :mnth = 99)")
    Integer countDiarySize(final @Param("tagNo") Integer tagNo, final @Param("yy") Integer yy, final @Param("mnth") Integer mnth);
}
