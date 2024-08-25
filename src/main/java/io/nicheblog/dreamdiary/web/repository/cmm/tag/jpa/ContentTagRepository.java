package io.nicheblog.dreamdiary.web.repository.cmm.tag.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * ContentTagRepository
 * <pre>
 *  컨텐츠 태그 정보 repository 인터페이스
 *  (cascade하지 않고 수동 관리)
 * </pre>
 *
 * @author nichefish
 */

@Repository("contentTagRepository")
public interface ContentTagRepository
        extends BaseStreamRepository<ContentTagEntity, Integer> {

    /**
     * obsolete된 기존 컨텐츠 태그 삭제:: 메소드 분리
     */
    @Modifying
    @Query("DELETE FROM ContentTagEntity ct " +
            "WHERE ct.refPostNo = :postNo AND ct.refContentType = :contentType " +
            "AND EXISTS (SELECT 1 FROM TagEntity t WHERE t.tagNo = ct.refTagNo AND t.tagNm = :tagNm AND (t.ctgr = :ctgr OR (t.ctgr IS NULL AND :ctgr IS NULL)))")
    void deleteObsoleteContentTags(Integer postNo, String contentType, String tagNm, String ctgr);
}
