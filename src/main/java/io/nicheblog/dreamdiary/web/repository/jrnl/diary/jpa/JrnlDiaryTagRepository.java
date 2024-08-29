package io.nicheblog.dreamdiary.web.repository.jrnl.diary.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryTagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

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

    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    Optional<JrnlDiaryTagEntity> findById(Integer key);

    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    Page<JrnlDiaryTagEntity> findAll(@Nullable Specification<JrnlDiaryTagEntity> spec, Pageable pageable);

    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    List<JrnlDiaryTagEntity> findAll(@Nullable Specification<JrnlDiaryTagEntity> spec);

    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    List<JrnlDiaryTagEntity> findAll(@Nullable Specification<JrnlDiaryTagEntity> spec, Sort sort);

    @Override
    @EntityGraph(value = "JrnlDiaryTagEntity.withTag")
    Stream<JrnlDiaryTagEntity> streamAllBy(@Nullable Specification<JrnlDiaryTagEntity> spec);

    /**
     * 년도/월별 저널 꿈 태그 개수 조회
     */
    @Query("SELECT COUNT(contentTag.contentTagNo) " +
            "FROM JrnlDiaryContentTagEntity contentTag " +
            "INNER JOIN FETCH JrnlDiaryEntity diary ON contentTag.refPostNo = diary.postNo " +
            "INNER JOIN FETCH JrnlDayEntity day ON diary.jrnlDayNo = day.postNo " +
            "WHERE contentTag.refTagNo = :tagNo " +
            " AND (:yy IS NULL OR day.yy = :yy OR :yy = 9999) " +
            " AND (:mnth IS NULL OR day.mnth = :mnth OR :mnth = 99)")
    Integer countDiarySize(Integer tagNo, Integer yy, Integer mnth);
}
