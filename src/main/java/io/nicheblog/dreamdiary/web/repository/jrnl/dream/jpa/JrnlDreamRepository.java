package io.nicheblog.dreamdiary.web.repository.jrnl.dream.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JrnlDreamRepository
 * <pre>
 *  저널 꿈 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlDreamRepository")
public interface JrnlDreamRepository
        extends BaseStreamRepository<JrnlDreamEntity, Integer> {

    /**
     * 해당 일자에서 꿈 마지막 인덱스 조회
     */
    @Query("SELECT MAX(dream.idx) " +
            "FROM JrnlDreamEntity dream " +
            "INNER JOIN JrnlDayEntity day ON dream.jrnlDayNo = day.postNo " +
            "WHERE dream.jrnlDayNo = :jrnlDayNo AND NOT(dream.elseDreamerNm = 'Y')")
    Optional<Integer> findLastIndexByJrnlDay(final @Param("jrnlDayNo") Integer jrnlDayNo);
}