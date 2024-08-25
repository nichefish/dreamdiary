package io.nicheblog.dreamdiary.web.repository.jrnl.dream.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamTagEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    /**
     * 년도/월별 저널 꿈 태그 개수 조회
     */
    @Query("SELECT COUNT(contentTag.contentTagNo) " +
            "FROM JrnlDreamContentTagEntity contentTag " +
            "INNER JOIN JrnlDreamEntity dream ON contentTag.refPostNo = dream.postNo " +
            "INNER JOIN JrnlDayEntity day ON dream.jrnlDayNo = day.postNo " +
            "WHERE contentTag.refTagNo = :tagNo " +
            " AND (:yy IS NULL OR day.yy = :yy OR :yy = 9999) " +
            " AND (:mnth IS NULL OR day.mnth = :mnth OR :mnth = 99)")
    Integer countDreamSize(Integer tagNo, Integer yy, Integer mnth);
}
