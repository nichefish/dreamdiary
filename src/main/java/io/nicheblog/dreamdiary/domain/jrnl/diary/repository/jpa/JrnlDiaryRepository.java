package io.nicheblog.dreamdiary.domain.jrnl.diary.repository.jpa;

import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
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
 * JrnlDiaryRepository
 * <pre>
 *  저널 일기 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlDiaryRepository")
public interface JrnlDiaryRepository
        extends BaseStreamRepository<JrnlDiaryEntity, Integer> {

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    Optional<JrnlDiaryEntity> findById(Integer key);

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    Page<JrnlDiaryEntity> findAll(@Nullable Specification<JrnlDiaryEntity> spec, Pageable pageable);

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    List<JrnlDiaryEntity> findAll(@Nullable Specification<JrnlDiaryEntity> spec);

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    List<JrnlDiaryEntity> findAll(@Nullable Specification<JrnlDiaryEntity> spec, Sort sort);

    /** entityGraph */
    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    Stream<JrnlDiaryEntity> streamAllBy(@Nullable Specification<JrnlDiaryEntity> spec);

    /**
     * 해당 일자에서 일기 마지막 인덱스 조회
     *
     * @param jrnlDayNo 조회할 일자 번호
     * @return {@link Optional} -- 해당 일자에서 일기의 마지막 인덱스
     */
    @Query("SELECT MAX(diary.idx) " +
            "FROM JrnlDiaryEntity diary " +
            "INNER JOIN FETCH JrnlDayEntity day ON diary.jrnlDayNo = day.postNo " +
            "WHERE diary.jrnlDayNo = :jrnlDayNo")
    Optional<Integer> findLastIndexByJrnlDay(final @Param("jrnlDayNo") Integer jrnlDayNo);
}