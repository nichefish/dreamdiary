package io.nicheblog.dreamdiary.web.repository.jrnl.diary;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryTagEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * JrnlDiaryTagRepository
 * <pre>
 *  게시판 태그 정보 repository 인터페이스
 *  ※게시판 태그(board_tag) = 게시판별 태그 정보. 일반게시판 게시물 태그(board_post_tag)와 1:N으로 연관된다.
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
