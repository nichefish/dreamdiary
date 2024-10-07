package io.nicheblog.dreamdiary.web.repository.cmm.tag.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TagRepository
 * <pre>
 *  태그 정보 repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */

@Repository("tagRepository")
public interface TagRepository
        extends BaseStreamRepository<TagEntity, Integer> {

    /**
     * 태그명으로 테이블 조회
     */
    Optional<TagEntity> findByTagNm(final String tagNo);
    
    /**
     * 태그명 + 카테고리명으로 테이블 조회
     */
    Optional<TagEntity> findByTagNmAndCtgr(final String tagNm, final String ctgr);

    /**
     * 년도/월별 저널 꿈 태그 개수 조회
     */
    @Query("SELECT COUNT(contentTag.contentTagNo) " +
            "FROM ContentTagEntity contentTag " +
            "INNER JOIN TagEntity tag ON tag.tagNo = contentTag.refTagNo " +
            "WHERE contentTag.refTagNo = :tagNo " +
            " AND (:refContentType IS NULL OR :refContentType = '' OR contentTag.refContentType = :refContentType)")
    Integer countTagSize(final @Param("tagNo") Integer tagNo, final @Param("refContentType") String refContentType);
}
