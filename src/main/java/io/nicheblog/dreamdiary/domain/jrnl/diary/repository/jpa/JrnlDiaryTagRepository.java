package io.nicheblog.dreamdiary.domain.jrnl.diary.repository.jpa;

import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryTagEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * JrnlDiaryTagRepository
 * <pre>
 *  저널 일기 태그 정보 repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlDiaryTagRepository")
public interface JrnlDiaryTagRepository
        extends BaseStreamRepository<JrnlDiaryTagEntity, Integer> {

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    Optional<JrnlDiaryTagEntity> findById(Integer key);

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    Page<JrnlDiaryTagEntity> findAll(@Nullable Specification<JrnlDiaryTagEntity> spec, Pageable pageable);

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    List<JrnlDiaryTagEntity> findAll(@Nullable Specification<JrnlDiaryTagEntity> spec);

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    List<JrnlDiaryTagEntity> findAll(@Nullable Specification<JrnlDiaryTagEntity> spec, Sort sort);

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    Stream<JrnlDiaryTagEntity> streamAllBy(@Nullable Specification<JrnlDiaryTagEntity> spec);

    /**
     * 년도/월별 저널 일기 태그 개수 조회
     *
     * @param tagNo - 조회할 태그 번호
     * @param yy - 조회할 년도
     * @param mnth - 조회할 월
     * @return {@link Integer} -- 태그 번호와 년도, 월에 해당하는 태그 개수
     */
    @Transactional(readOnly = true)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT COUNT(contentTag.contentTagNo) " +
            "FROM JrnlDiaryContentTagEntity contentTag " +
            "INNER JOIN FETCH JrnlDiaryEntity diary ON contentTag.refPostNo = diary.postNo " +
            "INNER JOIN FETCH JrnlDayEntity day ON diary.jrnlDayNo = day.postNo " +
            "WHERE contentTag.refTagNo = :tagNo " +
            " AND (:yy IS NULL OR day.yy = :yy OR :yy = 9999) " +
            " AND (:mnth IS NULL OR day.mnth = :mnth OR :mnth = 99)")
    Integer countDiarySize(final @Param("tagNo") Integer tagNo, final @Param("yy") Integer yy, final @Param("mnth") Integer mnth);
}
