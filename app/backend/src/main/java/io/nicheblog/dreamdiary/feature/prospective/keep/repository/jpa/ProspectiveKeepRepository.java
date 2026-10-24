package io.nicheblog.dreamdiary.feature.prospective.keep.repository.jpa;

import io.nicheblog.dreamdiary.feature.prospective.keep.entity.ProspectiveKeepEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.util.Optional;
import java.util.List;

/**
 * ProspectiveKeepRepository
 * <pre>
 *  Prospective Keep (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository
public interface ProspectiveKeepRepository
        extends BaseStreamRepository<ProspectiveKeepEntity, Integer> {

    /**
     * 사용자의 마지막 Keep 순번 조회 (월 무관 전역).
     *
     * @param createdBy 등록자 ID
     * @return {@link Optional} -- 해당 사용자의 최대 sortOrder
     */
    @Transactional(readOnly = true)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT MAX(keep.sortOrder) " +
            "FROM ProspectiveKeepEntity keep " +
            "WHERE keep.createdBy = :createdBy")
    Optional<Integer> findLastIndexByCreatedBy(final @Param("createdBy") String createdBy);

    /** Project 삭제 시 해당 사용자의 Keep 연결만 미지정 상태로 되돌린다. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProspectiveKeepEntity keep SET keep.project = NULL " +
            "WHERE keep.project.id = :projectId AND keep.createdBy = :createdBy")
    int clearProjectByProjectIdAndCreatedBy(
            final @Param("projectId") Integer projectId,
            final @Param("createdBy") String createdBy
    );

    /** Project 삭제 전 상세 캐시 무효화 대상 Keep ID를 조회한다. */
    @Query("SELECT keep.id FROM ProspectiveKeepEntity keep " +
            "WHERE keep.project.id = :projectId AND keep.createdBy = :createdBy")
    List<Integer> findIdsByProjectIdAndCreatedBy(
            final @Param("projectId") Integer projectId,
            final @Param("createdBy") String createdBy
    );
}
