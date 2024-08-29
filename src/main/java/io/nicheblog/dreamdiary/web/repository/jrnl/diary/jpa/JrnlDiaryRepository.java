package io.nicheblog.dreamdiary.web.repository.jrnl.diary.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
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
 *  저널 일기 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlDiaryRepository")
public interface JrnlDiaryRepository
        extends BaseStreamRepository<JrnlDiaryEntity, Integer> {

    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    Optional<JrnlDiaryEntity> findById(Integer key);

    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    Page<JrnlDiaryEntity> findAll(@Nullable Specification<JrnlDiaryEntity> spec, Pageable pageable);

    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    List<JrnlDiaryEntity> findAll(@Nullable Specification<JrnlDiaryEntity> spec);

    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    List<JrnlDiaryEntity> findAll(@Nullable Specification<JrnlDiaryEntity> spec, Sort sort);

    @Override
    @EntityGraph(value = "JrnlDiaryEntity.withAll")
    Stream<JrnlDiaryEntity> streamAllBy(@Nullable Specification<JrnlDiaryEntity> spec);

    /**
     * 해당 일자에서 일기 마지막 인덱스 조회 
     */
    @Query("SELECT MAX(diary.idx) " +
            "FROM JrnlDiaryEntity diary " +
            "INNER JOIN FETCH JrnlDayEntity day ON diary.jrnlDayNo = day.postNo " +
            "WHERE diary.jrnlDayNo = :jrnlDayNo")
    Optional<Integer> findLastIndexByJrnlDay(final @Param("jrnlDayNo") Integer jrnlDayNo);
}