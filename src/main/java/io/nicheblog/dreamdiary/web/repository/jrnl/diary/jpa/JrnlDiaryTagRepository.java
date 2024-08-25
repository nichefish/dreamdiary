package io.nicheblog.dreamdiary.web.repository.jrnl.diary.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryTagEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    /**
     * 년도/월별 저널 꿈 태그 개수 조회
     */
    @Query("SELECT COUNT(contentTag.contentTagNo) " +
            "FROM JrnlDiaryContentTagEntity contentTag " +
            "INNER JOIN JrnlDiaryEntity diary ON contentTag.refPostNo = diary.postNo " +
            "INNER JOIN JrnlDayEntity day ON diary.jrnlDayNo = day.postNo " +
            "WHERE contentTag.refTagNo = :tagNo " +
            " AND (:yy IS NULL OR day.yy = :yy OR :yy = 9999) " +
            " AND (:mnth IS NULL OR day.mnth = :mnth OR :mnth = 99)")
    Integer countDiarySize(Integer tagNo, Integer yy, Integer mnth);
}
