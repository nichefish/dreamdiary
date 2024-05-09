package io.nicheblog.dreamdiary.web.repository.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
            "WHERE ct.refPostNo = :postNo AND ct.refContentType = :contentType AND ct.refTagNo IN (SELECT t.tagNo FROM TagEntity t WHERE t.tagNm IN :obsoleteTagList)")
    void deleteObsoleteContentTags(final @Param("postNo") Integer postNo, final @Param("contentType") String contentType, final @Param("obsoleteTagList") List<String> obsoleteTagList);
}
