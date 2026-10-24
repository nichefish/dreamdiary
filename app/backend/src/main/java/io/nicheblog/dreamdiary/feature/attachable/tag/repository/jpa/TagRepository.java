package io.nicheblog.dreamdiary.feature.attachable.tag.repository.jpa;

import io.nicheblog.dreamdiary.feature.attachable.tag.entity.TagEntity;
import io.nicheblog.dreamdiary.feature.attachable.tag.model.TagDto;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

/**
 * TagRepository
 * <pre>
 *  태그 정보 repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository
public interface TagRepository
        extends BaseStreamRepository<TagEntity, Integer> {

    /**
     * 사용자가 특정 콘텐츠 타입에 실제로 사용한 태그를 (태그명, 카테고리명) 행으로 조회한다.
     * <pre>
     *  Tagify 카테고리 자동완성 맵의 원천이다. 카테고리가 없는 태그는 빈 문자열로 내려간다.
     *  콘텐츠 타입별 연관을 갖는 전용 태그 뷰 엔티티 없이 tag_content 를 직접 조인하므로
     *  단일 content_type 을 쓰는 콘텐츠(할일 등)가 그대로 사용할 수 있다.
     * </pre>
     *
     * @param username 사용자 계정명
     * @param contentType 콘텐츠 타입 (tag_content.ref_content_type)
     * @return (태그명, 카테고리명) 행 목록
     */
    @Transactional(readOnly = true)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT DISTINCT new io.nicheblog.dreamdiary.feature.attachable.tag.model.TagDto(tag.id, tag.name, COALESCE(category.name, '')) " +
            "FROM TagEntity tag " +
            "INNER JOIN TagContentEntity tagContent ON tagContent.tagId = tag.id " +
            "LEFT JOIN tag.tagCategory category " +
            "WHERE tagContent.createdBy = :username " +
            "  AND tagContent.refContentType = :contentType")
    List<TagDto> findCategoryRowsByUserAndContentType(
            final @Param("username") String username,
            final @Param("contentType") String contentType
    );

    /**
     * 태그명 + 카테고리명으로 조회
     *
     * @param name 조회할 태그명
     * @param ctgr 조회할 카테고리명
     * @return 태그명과 카테고리명에 해당하는 TagEntity를 포함하는 Optional 객체
     */
    @Query(value = "SELECT t.* " +
        "FROM tag t " +
        "LEFT JOIN tag_category tc ON tc.id = t.tag_category_id AND tc.deleted_at IS NULL " +
        "WHERE t.name = :name " +
        "  AND t.deleted_at IS NULL " +
        "  AND ( " +
        "       ((:ctgr IS NULL OR :ctgr = '') AND t.tag_category_id IS NULL) " +
        "       OR tc.name = :ctgr " +
        "  )", nativeQuery = true)
    Optional<TagEntity> findByNameAndCtgr(final String name, final String ctgr);

    /**
     * 태그 ID로 조회
     *
     * @param ids 태그 ID 목록
     * @return 태그명과 카테고리명에 해당하는 TagEntity를 포함하는 Optional 객체
     */
    List<TagEntity> findAllByIdIn(List<Integer> ids);

    /**
     * 컨텐츠 타입별 태그 개수 조회
     *
     * @param tagId - 조회할 태그 ID
     * @param refContentType - 조회할 컨텐츠 타입 (필터링 조건, null 또는 빈 문자열일 경우 조건 무시)
     * @return Integer - 태그 ID와 컨텐츠 타입에 해당하는 태그 개수
     */
    @Transactional(readOnly = true)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT COUNT(ct.id) " +
            "FROM TagContentEntity ct " +
            "INNER JOIN fetch TagEntity tag ON tag.id = ct.tagId " +
            "WHERE ct.tagId = :tagId " +
            " AND (:refContentType IS NULL OR :refContentType = '' OR ct.refContentType = :refContentType)" +
            " AND (ct.createdBy = :createdBy)")
    Integer countTagSize(final @Param("tagId") Integer tagId, final @Param("refContentType") String refContentType, final String createdBy);
}

