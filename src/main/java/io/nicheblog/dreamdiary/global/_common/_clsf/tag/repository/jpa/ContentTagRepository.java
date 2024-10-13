package io.nicheblog.dreamdiary.global._common._clsf.tag.repository.jpa;

import io.nicheblog.dreamdiary.global._common._clsf.tag.entity.ContentTagEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * ContentTagRepository
 * <pre>
 *  컨텐츠 태그 정보 repository 인터페이스.
 *  (cascade하지 않고 수동 관리)
 * </pre>
 *
 * @author nichefish
 */
@Repository("contentTagRepository")
public interface ContentTagRepository
        extends BaseStreamRepository<ContentTagEntity, Integer> {

    /**
     * 특정 게시물에 대해 태그 정보와 연결되지 않는 컨텐츠 태그 삭제.
     *
     * @param postNo - 삭제할 대상 컨텐츠 태그의 게시글 번호
     * @param contentType - 삭제할 대상의 컨텐츠 타입
     * @param tagNm - 삭제할 태그의 이름
     * @param ctgr - 삭제할 태그의 카테고리
     */
    @Modifying
    @Query("DELETE FROM ContentTagEntity ct " +
            "WHERE ct.refPostNo = :postNo AND ct.refContentType = :contentType " +
            "AND EXISTS (SELECT 1 FROM TagEntity t WHERE t.tagNo = ct.refTagNo AND t.tagNm = :tagNm AND (t.ctgr = :ctgr OR (t.ctgr IS NULL AND :ctgr IS NULL)))")
    void deleteObsoleteContentTags(final @Param("postNo") Integer postNo, final @Param("contentType") String contentType, final @Param("tagNm") String tagNm, final @Param("ctgr") String ctgr);
}
