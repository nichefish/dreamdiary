package io.nicheblog.dreamdiary.feature.prospective.project.repository.jpa;

import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Prospective Project JPA 저장소. */
@Repository
public interface ProspectiveProjectRepository extends BaseStreamRepository<ProspectiveProjectEntity, Integer> {

    /** 사용자의 마지막 Project 순번을 조회한다. */
    @Query("SELECT MAX(project.sortOrder) FROM ProspectiveProjectEntity project WHERE project.createdBy = :createdBy")
    Optional<Integer> findLastIndexByCreatedBy(final @Param("createdBy") String createdBy);

    /** 사용자의 활성 Project를 정렬 계약대로 조회한다. */
    List<ProspectiveProjectEntity> findAllByCreatedByOrderBySortOrderAscIdAsc(final String createdBy);

    /** 요청 ID 중 현재 사용자가 소유한 활성 Project를 조회한다. */
    List<ProspectiveProjectEntity> findAllByIdInAndCreatedBy(final List<Integer> idList, final String createdBy);
}
