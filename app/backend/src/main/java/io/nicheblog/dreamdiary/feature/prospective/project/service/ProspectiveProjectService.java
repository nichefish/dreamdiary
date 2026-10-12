package io.nicheblog.dreamdiary.feature.prospective.project.service;

import io.nicheblog.dreamdiary.auth.security.exception.NotAuthorizedException;
import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.feature.attachable._shared.entity.BaseAttachableKey;
import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.LifecycleKey;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.model.cmpstn.LifecycleCmpstn;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.service.LifecycleService;
import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.PrefixContentEntity;
import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.PrefixEntity;
import io.nicheblog.dreamdiary.feature.attachable.prefix.model.PrefixDto;
import io.nicheblog.dreamdiary.feature.attachable.prefix.repository.jpa.PrefixContentRepository;
import io.nicheblog.dreamdiary.feature.attachable.prefix.service.PrefixContentService;
import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.feature.prospective.project.mapstruct.ProspectiveProjectMapstruct;
import io.nicheblog.dreamdiary.feature.prospective.project.model.ProspectiveProjectDto;
import io.nicheblog.dreamdiary.feature.prospective.project.repository.jpa.ProspectiveProjectRepository;
import io.nicheblog.dreamdiary.feature.prospective.project.spec.ProspectiveProjectSpec;
import io.nicheblog.dreamdiary.feature.prospective.keep.repository.jpa.ProspectiveKeepRepository;
import io.nicheblog.dreamdiary.feature.prospective.keep.service.strategy.ProspectiveKeepCacheEvictor;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseDtoReadableService;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseDtoWritableService;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * ProspectiveProjectService
 * <pre>
 *  로그인 사용자가 소유한 Prospective Project의 CRUD와 전역 순서를 관리한다.
 *  Project 이름 앞 개인 말머리(0..1)는 prefix_content(PROSPECTIVE_PROJECT)로 연결한다.
 * </pre>
 *
 * @author nichefish
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ProspectiveProjectService
        implements BaseDtoReadableService<ProspectiveProjectDto, Integer, ProspectiveProjectEntity>,
                BaseDtoWritableService<ProspectiveProjectDto, ProspectiveProjectDto, Integer, ProspectiveProjectEntity> {

    @Getter
    private final ProspectiveProjectRepository repository;
    @Getter
    private final ProspectiveProjectSpec spec;
    private final ProspectiveKeepRepository prospectiveKeepRepository;
    private final ProspectiveKeepCacheEvictor prospectiveKeepCacheEvictor;
    private final LifecycleService lifecycleService;
    private final PrefixContentService prefixContentService;
    private final PrefixContentRepository prefixContentRepository;
    @Getter
    private final ProspectiveProjectMapstruct mapstruct = ProspectiveProjectMapstruct.INSTANCE;

    @Override
    public ProspectiveProjectMapstruct getReadMapstruct() {
        return this.mapstruct;
    }

    @Override
    public ProspectiveProjectMapstruct getWriteMapstruct() {
        return this.mapstruct;
    }

    /** 현재 로그인 사용자의 Project 목록을 전역 순서대로 반환한다. */
    @Transactional(readOnly = true)
    public List<ProspectiveProjectDto> getMyList() {
        final String username = AuthUtils.requireLoginUsername();
        final List<ProspectiveProjectDto> projectList =
                mapstruct.toDtoList(repository.findAllByCreatedByOrderBySortOrderAscIdAsc(username));
        final List<ProspectiveProjectDto> result = projectList == null ? Collections.emptyList() : projectList;
        this.applyProjectLifecycles(result);
        this.applyProjectPrefixes(result);
        return result;
    }

    /** 현재 로그인 사용자가 소유한 Project 상세를 반환한다. */
    @Transactional(readOnly = true)
    public ProspectiveProjectDto getMyDetail(final Integer id) throws Exception {
        final ProspectiveProjectEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("prospective.project.not-found"));
        this.assertOwned(entity, "detail");
        final ProspectiveProjectDto dto = mapstruct.toDto(entity);
        this.applyProjectLifecycles(List.of(dto));
        this.applyProjectPrefixes(List.of(dto));
        return dto;
    }

    /**
     * Project 본문과 개인 말머리 선택을 같은 트랜잭션에서 등록한다.
     *
     * @param registDto 등록할 객체 (prefixId: 선택 말머리, null이면 없음)
     * @return {@link ServiceResponse} -- 등록 결과 객체 (prefix 포함)
     */
    @Override
    @Transactional
    public ServiceResponse regist(final ProspectiveProjectDto registDto) throws Exception {
        final ServiceResponse response = BaseDtoWritableService.super.regist(registDto);
        this.applyPrefixSelection((ProspectiveProjectDto) response.getRsltObj(), registDto.getPrefixId());
        return response;
    }

    /**
     * Project 본문과 개인 말머리 선택을 같은 트랜잭션에서 수정한다.
     * multipart 요청에서 prefixId가 비어 있으면 선택 해제로 해석하므로 클라이언트는 항상 현재 선택을 보낸다.
     *
     * @param modifyDto 수정할 객체
     * @return {@link ServiceResponse} -- 수정 결과 객체 (prefix 포함)
     */
    @Override
    @Transactional
    public ServiceResponse modify(final ProspectiveProjectDto modifyDto) throws Exception {
        final ServiceResponse response = BaseDtoWritableService.super.modify(modifyDto);
        this.applyPrefixSelection((ProspectiveProjectDto) response.getRsltObj(), modifyDto.getPrefixId());
        return response;
    }

    /** 등록 시 사용자 목록의 마지막 순번 다음 값을 부여한다. */
    @Override
    public void preRegist(final ProspectiveProjectDto registDto) {
        final String username = AuthUtils.requireLoginUsername();
        registDto.setSortOrder(repository.findLastIndexByCreatedBy(username).orElse(0) + 1);
    }

    /** 수정 대상 소유권을 검증하며 기존 순번은 별도 정렬 API만 변경한다. */
    @Override
    public void preModify(final ProspectiveProjectDto modifyDto, final ProspectiveProjectEntity modifyEntity) {
        this.assertOwned(modifyEntity, "modify");
        modifyDto.setSortOrder(modifyEntity.getSortOrder());
    }

    /** 삭제 대상 소유권을 검증한다. */
    @Override
    public void preDelete(final ProspectiveProjectDto deletedDto) {
        if (!AuthUtils.isCreatedBy(deletedDto.getCreatedBy())) {
            log.warn("Prospective Project delete rejected: id={}, owner={}", deletedDto.getId(), deletedDto.getCreatedBy());
            throw new NotAuthorizedException("common.result.access-not-authorized");
        }
    }

    /** Project soft-delete 직전에 연결된 Keep을 Project 미지정 상태로 되돌린다. */
    @Override
    public void preRemove(final ProspectiveProjectEntity deleteEntity) throws Exception {
        final List<Integer> linkedKeepIds = prospectiveKeepRepository.findIdsByProjectIdAndCreatedBy(
                deleteEntity.getId(), deleteEntity.getCreatedBy());
        final int clearedCount = prospectiveKeepRepository.clearProjectByProjectIdAndCreatedBy(
                deleteEntity.getId(), deleteEntity.getCreatedBy());
        for (final Integer keepId : linkedKeepIds) {
            prospectiveKeepCacheEvictor.evictAfterCommit(deleteEntity.getCreatedBy(), keepId);
        }
        log.info("Prospective Project Keep links cleared: projectId={}, owner={}, count={}",
                deleteEntity.getId(), deleteEntity.getCreatedBy(), clearedCount);
    }

    /** 삭제 뒤 남은 활성 Project 순서를 다시 1..N으로 수렴시킨다. */
    @Override
    public void postDelete(final ProspectiveProjectDto deletedDto) {
        final List<ProspectiveProjectEntity> remaining =
                repository.findAllByCreatedByOrderBySortOrderAscIdAsc(deletedDto.getCreatedBy());
        for (int index = 0; index < remaining.size(); index++) {
            remaining.get(index).setSortOrder(index + 1);
        }
        repository.saveAll(remaining);
        log.info("Prospective Project delete order normalized: user={}, count={}",
                deletedDto.getCreatedBy(), remaining.size());
    }

    /**
     * 사용자의 soft-delete되지 않은 Project 전체 순서를 요청 ID 순서와 일치시킨다.
     * 부분 목록·중복 ID·타인 ID가 섞인 요청은 순서를 변경하지 않고 거부한다.
     */
    @Transactional
    public void reorderMyProjects(final List<Integer> orderedIds) {
        final String username = AuthUtils.requireLoginUsername();
        final List<ProspectiveProjectEntity> ownedProjects = repository.findAllByCreatedByOrderBySortOrderAscIdAsc(username);
        final Set<Integer> ownedIds = new HashSet<>();
        ownedProjects.forEach(project -> ownedIds.add(project.getId()));
        final Set<Integer> requestedIds = orderedIds == null ? Collections.emptySet() : new HashSet<>(orderedIds);

        if (orderedIds == null || orderedIds.size() != requestedIds.size() || !ownedIds.equals(requestedIds)) {
            log.warn("Prospective Project reorder rejected: user={}, ownedCount={}, requestedCount={}",
                    username, ownedIds.size(), orderedIds == null ? null : orderedIds.size());
            throw new IllegalArgumentException("prospective.project.reorder.invalid");
        }

        final Map<Integer, ProspectiveProjectEntity> projectMap = new HashMap<>();
        ownedProjects.forEach(project -> projectMap.put(project.getId(), project));
        for (int index = 0; index < orderedIds.size(); index++) {
            projectMap.get(orderedIds.get(index)).setSortOrder(index + 1);
        }
        repository.saveAll(ownedProjects);
        log.info("Prospective Project reorder completed: user={}, count={}", username, orderedIds.size());
    }

    /** Project 원본의 작성자가 현재 로그인 사용자인지 검증한다. */
    private void assertOwned(final ProspectiveProjectEntity entity, final String action) {
        if (AuthUtils.isCreatedBy(entity.getCreatedBy())) return;
        log.warn("Prospective Project {} rejected: id={}, owner={}", action, entity.getId(), entity.getCreatedBy());
        throw new NotAuthorizedException("common.result.access-not-authorized");
    }

    /** Project DTO 목록에 공통 라이프사이클 현재값을 부착하며 저장 행이 없으면 OPEN으로 해석한다. */
    private void applyProjectLifecycles(final List<ProspectiveProjectDto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) return;
        final List<Integer> projectIds = dtoList.stream()
                .map(ProspectiveProjectDto::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (projectIds.isEmpty()) return;
        final Map<Integer, String> lifecycleMap =
                lifecycleService.getLifecycleMap(ContentType.PROSPECTIVE_PROJECT, projectIds);
        for (final ProspectiveProjectDto dto : dtoList) {
            if (dto == null || dto.getId() == null) continue;
            final LifecycleKey key = LifecycleKey.getByKey(lifecycleMap.get(dto.getId()));
            dto.setLifecycle(LifecycleCmpstn.of(key == null ? LifecycleKey.OPEN : key));
        }
    }

    /**
     * 선택한 말머리의 소유·활성 상태를 검증하고 prefix_content 연결을 반영한다.
     * 기존 선택과 같은 비활성 말머리는 유지할 수 있고, 신규 비활성·타인 말머리는 거부된다.
     */
    private void applyPrefixSelection(final ProspectiveProjectDto dto, final Integer requestedPrefixId) {
        if (dto == null || dto.getId() == null) return;
        final PrefixEntity prefix = prefixContentService.applySelection(
                new BaseAttachableKey(dto.getId(), ContentType.PROSPECTIVE_PROJECT),
                ContentType.PROSPECTIVE_PROJECT.key,
                requestedPrefixId
        );
        dto.setPrefix(toPrefixDto(prefix));
        dto.setPrefixId(prefix == null ? null : prefix.getId());
    }

    /** Project DTO 목록에 선택된 개인 말머리를 일괄 부착한다. 연결이 없으면 null. */
    private void applyProjectPrefixes(final List<ProspectiveProjectDto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) return;
        final List<Integer> projectIds = dtoList.stream()
                .filter(Objects::nonNull)
                .map(ProspectiveProjectDto::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (projectIds.isEmpty()) return;
        final Map<Integer, PrefixEntity> prefixMap = new HashMap<>();
        for (final PrefixContentEntity link : prefixContentRepository.findAllByRefContentTypeAndRefIdIn(
                ContentType.PROSPECTIVE_PROJECT.key, projectIds)) {
            if (link.getPrefix() != null) prefixMap.putIfAbsent(link.getRefId(), link.getPrefix());
        }
        for (final ProspectiveProjectDto dto : dtoList) {
            if (dto == null || dto.getId() == null) continue;
            final PrefixEntity prefix = prefixMap.get(dto.getId());
            dto.setPrefix(toPrefixDto(prefix));
            dto.setPrefixId(prefix == null ? null : prefix.getId());
        }
    }

    /** 표시용 말머리 DTO. 비활성 말머리도 기존 선택이면 그대로 표시한다. */
    private static PrefixDto toPrefixDto(final PrefixEntity prefix) {
        if (prefix == null) return null;
        return PrefixDto.displayOf(
                prefix.getId(),
                prefix.getName(),
                prefix.getTextClass(),
                prefix.getSortOrder(),
                prefix.getActiveYn()
        );
    }
}
