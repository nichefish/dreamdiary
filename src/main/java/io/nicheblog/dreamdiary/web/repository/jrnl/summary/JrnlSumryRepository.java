package io.nicheblog.dreamdiary.web.repository.jrnl.summary;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JrnlSumryRepository
 * <pre>
 *  저널 결산 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlSumryRepository")
public interface JrnlSumryRepository
        extends BaseStreamRepository<JrnlSumryEntity, Integer> {

    /**
     * 년도로 결산 정보 조회
     */
    Optional<JrnlSumryEntity> findByYy(Integer yy);

    /**
     * 년도별 꿈 일자 개수 조회
     */
    @Query("SELECT COUNT(day.postNo) " +
            "FROM JrnlDayEntity day " +
            "INNER JOIN JrnlDreamEntity dream ON day.postNo = dream.jrnlDayNo " +
            "WHERE YEAR(CASE WHEN day.dtUnknownYn = 'Y' THEN day.aprxmtDt ELSE day.jrnlDt END) = :yy")
    Integer getDreamDayCntByYy(Integer yy);

    /**
     * 년도별 꿈 개수 조회
     */
    @Query("SELECT COUNT(dream.postNo) " +
            "FROM JrnlDreamEntity dream " +
            "INNER JOIN JrnlDayEntity day ON dream.jrnlDayNo = day.postNo " +
            "WHERE YEAR(CASE WHEN day.dtUnknownYn = 'Y' THEN day.aprxmtDt ELSE day.jrnlDt END) = :yy")
    Integer getDreamCntByYy(Integer yy);

    /**
     * 년도별 일기 일자 개수 조회
     */
    @Query("SELECT COUNT(day.postNo) " +
            "FROM JrnlDayEntity day " +
            "INNER JOIN JrnlDiaryEntity diary ON day.postNo = diary.jrnlDayNo " +
            "WHERE YEAR(CASE WHEN day.dtUnknownYn = 'Y' THEN day.aprxmtDt ELSE day.jrnlDt END) = :yy")
    Integer getDiaryDayCntByYy(Integer yy);

}