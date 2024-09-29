package io.nicheblog.dreamdiary.web.repository.jrnl.dream.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamTagEntity;
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
 * JrnlDreamTagRepository
 * <pre>
 *  저널 꿈 태그 정보 repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */

@Repository("jrnlDreamTagRepository")
public interface JrnlDreamTagRepository
        extends BaseStreamRepository<JrnlDreamTagEntity, Integer> {

    @Override
    @EntityGraph(value = "JrnlDreamTagEntity.withTag")
    Optional<JrnlDreamTagEntity> findById(Integer key);

    @Override
    @EntityGraph(value = "JrnlDreamTagEntity.withTag")
    Page<JrnlDreamTagEntity> findAll(@Nullable Specification<JrnlDreamTagEntity> spec, Pageable pageable);

    @Override
    @EntityGraph(value = "JrnlDreamTagEntity.withTag")
    List<JrnlDreamTagEntity> findAll(@Nullable Specification<JrnlDreamTagEntity> spec);

    @Override
    @EntityGraph(value = "JrnlDreamTagEntity.withTag")
    List<JrnlDreamTagEntity> findAll(@Nullable Specification<JrnlDreamTagEntity> spec, Sort sort);

    @Override
    @EntityGraph(value = "JrnlDreamTagEntity.withTag")
    Stream<JrnlDreamTagEntity> streamAllBy(@Nullable Specification<JrnlDreamTagEntity> spec);
    
    /**
     * 년도/월별 저널 꿈 태그 개수 조회
     */
    @Query("SELECT COUNT(contentTag.contentTagNo) " +
            "FROM JrnlDreamContentTagEntity contentTag " +
            "INNER JOIN FETCH JrnlDreamEntity dream ON contentTag.refPostNo = dream.postNo " +
            "INNER JOIN FETCH JrnlDayEntity day ON dream.jrnlDayNo = day.postNo " +
            "WHERE contentTag.refTagNo = :tagNo " +
            " AND (:yy IS NULL OR day.yy = :yy OR :yy = 9999) " +
            " AND (:mnth IS NULL OR day.mnth = :mnth OR :mnth = 99)")
    Integer countDreamSize(final @Param("tagNo") Integer tagNo, final @Param("yy") Integer yy, final @Param("mnth") Integer mnth);
}
