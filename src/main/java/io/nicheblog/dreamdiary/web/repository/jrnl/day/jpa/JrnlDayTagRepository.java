package io.nicheblog.dreamdiary.web.repository.jrnl.day.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayTagEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    /**
     * 년도/월별 저널 꿈 태그 개수 조회
     */
    @Query("SELECT COUNT(contentTag.contentTagNo) " +
            "FROM JrnlDayContentTagEntity contentTag " +
            "INNER JOIN JrnlDayEntity day ON contentTag.refPostNo = day.postNo " +
            "WHERE contentTag.refTagNo = :tagNo " +
            " AND (:yy IS NULL OR day.yy = :yy OR :yy = 9999) " +
            " AND (:mnth IS NULL OR day.mnth = :mnth OR :mnth = 99)")
    Integer countDiarySize(Integer tagNo, Integer yy, Integer mnth);
}
