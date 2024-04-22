package io.nicheblog.dreamdiary.web.repository.jrnl.diary;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    
    /**
     * 해당 일자에서 일기 마지막 인덱스 조회 
     */
    @Query("SELECT MAX(diary.idx) " +
            "FROM JrnlDiaryEntity diary " +
            "INNER JOIN JrnlDayEntity day ON diary.jrnlDayNo = day.postNo " +
            "WHERE diary.jrnlDayNo = :jrnlDayNo")
    Optional<Integer> findLastIndexByJrnlDay(final @Param("jrnlDayNo") Integer jrnlDayNo);
}