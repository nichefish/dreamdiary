package io.nicheblog.dreamdiary.feature.prospective.keep.service;

import io.nicheblog.dreamdiary.auth.security.exception.NotAuthorizedException;
import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.feature.attachable._shared.entity.BaseAttachableKey;
import io.nicheblog.dreamdiary.feature.attachable._shared.service.BaseAttachableService;
import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.PrefixEntity;
import io.nicheblog.dreamdiary.feature.attachable.prefix.model.PrefixDto;
import io.nicheblog.dreamdiary.feature.attachable.prefix.service.PrefixContentService;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.model.cmpstn.LifecycleCmpstn;
import io.nicheblog.dreamdiary.feature.prospective.keep.entity.ProspectiveKeepEntity;
import io.nicheblog.dreamdiary.feature.prospective.keep.mapstruct.ProspectiveKeepMapstruct;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepDto;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepSearchParam;
import io.nicheblog.dreamdiary.feature.prospective.keep.repository.jpa.ProspectiveKeepRepository;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.LifecycleKey;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.service.LifecycleService;
import io.nicheblog.dreamdiary.feature.prospective.keep.service.strategy.ProspectiveKeepCacheEvictor;
import io.nicheblog.dreamdiary.feature.prospective.keep.spec.ProspectiveKeepSpec;
import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.feature.prospective.project.repository.jpa.ProspectiveProjectRepository;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ProspectiveKeepService
 * <pre>
 *  Prospective Keep 관리 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ProspectiveKeepService
        implements BaseAttachableService<ProspectiveKeepDto, ProspectiveKeepDto, Integer, ProspectiveKeepEntity> {

    @Getter
    private final ProspectiveKeepRepository repository;
    @Getter
    private final ProspectiveKeepSpec spec;
    @Getter
    private final ProspectiveKeepMapstruct mapstruct = ProspectiveKeepMapstruct.INSTANCE;

    public ProspectiveKeepMapstruct getReadMapstruct() {
        return this.mapstruct;
    }
    public ProspectiveKeepMapstruct getWriteMapstruct() {
        return this.mapstruct;
    }

    private final ProspectiveKeepCacheEvictor prospectiveKeepCacheEvictor;
    private final LifecycleService lifecycleService;
    private final PrefixContentService prefixContentService;
    private final ProspectiveProjectRepository prospectiveProjectRepository;

    private final ApplicationContext context;
    private ProspectiveKeepService getSelf() {
        return context.getBean(this.getClass());
    }

    /**
     * 활성 Keep 목록 조회 (dto level).
     * <p>
     * 월에 종속되지 않고 사용자의 활성 집합(OPEN·PENDING)을 시점 무관하게 투영한다.
     * RESOLVED 는 활성 투영에서 제외한다. 소규모 개인 데이터라 리스트 캐시 없이 매 조회
     * 신선하게 lifecycle 을 부착한다.
     * (RESOLVED 제외는 인메모리 필터다. 카디널리티가 커지면 lifecycle 조인으로 쿼리 단계로 내린다.)
     * </p>
     *
     * @param username 사용자 계정명
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 활성 Keep 목록
     */
    public List<ProspectiveKeepDto> getActiveListDtoByUser(final String username, final ProspectiveKeepSearchParam searchParam) throws Exception {
        return this.getListDtoWithLifecycleByUser(username, searchParam).stream()
                .filter(dto -> !isTerminal(dto))
                .collect(Collectors.toList());
    }

    /**
     * 사용자 전 범위 Keep 목록 조회 (dto level).
     * <p>
     * 시점 무관하게 사용자의 모든 Keep을 lifecycle 부착해 반환한다. 활성 집합과 달리
     * RESOLVED 를 제외하지 않는다 -- 전수관리(keep) 화면이 완료·미해결을 함께 투영하기 위함이다.
     * 소규모 개인 데이터라 리스트 캐시 없이 매 조회 신선하게 lifecycle 을 부착한다.
     * </p>
     *
     * @param username 사용자 계정명
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 사용자 전 범위 Keep 목록 (lifecycle 부착)
     */
    public List<ProspectiveKeepDto> getListDtoWithLifecycleByUser(final String username, final ProspectiveKeepSearchParam searchParam) throws Exception {
        if (searchParam.getProjectId() != null && Boolean.TRUE.equals(searchParam.getProjectUnassigned())) {
            log.warn("Prospective Keep project scope rejected: projectId={} and projectUnassigned=true",
                    searchParam.getProjectId());
            throw new IllegalArgumentException("prospective.keep.project-scope.invalid");
        }
        searchParam.setCreatedBy(AuthUtils.requireUsername(username));

        final List<ProspectiveKeepDto> listDto = this.getSelf().getListDto(searchParam);
        this.applyKeepLifecycles(listDto);
        return listDto;
    }

    /**
     * Keep 본문과 개인 말머리 선택을 같은 트랜잭션에서 등록한다.
     *
     * @param registDto 등록할 객체
     * @return {@link ServiceResponse} -- 등록 결과 객체
     */
    @Override
    @Transactional
    public ServiceResponse regist(final ProspectiveKeepDto registDto) throws Exception {
        final ProspectiveProjectEntity project = this.resolveOwnedProject(registDto.getProjectId());
        final ServiceResponse response = BaseAttachableService.super.regist(registDto);
        final ProspectiveKeepDto updatedDto = (ProspectiveKeepDto) response.getRsltObj();
        this.applyProjectSelection(updatedDto, project);
        this.applyPrefixSelection(updatedDto, registDto.getPrefixId());
        return response;
    }

    /**
     * Keep 본문과 개인 말머리 선택을 같은 트랜잭션에서 수정한다.
     *
     * @param modifyDto 수정할 객체
     * @return {@link ServiceResponse} -- 수정 결과 객체
     */
    @Override
    @Transactional
    public ServiceResponse modify(final ProspectiveKeepDto modifyDto) throws Exception {
        final ProspectiveProjectEntity project = this.resolveOwnedProject(modifyDto.getProjectId());
        final ServiceResponse response = BaseAttachableService.super.modify(modifyDto);
        final ProspectiveKeepDto updatedDto = (ProspectiveKeepDto) response.getRsltObj();
        this.applyProjectSelection(updatedDto, project);
        this.applyPrefixSelection(updatedDto, modifyDto.getPrefixId());
        return response;
    }

    /**
     * 요청한 Project가 현재 로그인 사용자의 활성 Project인지 검증한다.
     * null은 Project 미지정이라는 정상 상태다.
     */
    private ProspectiveProjectEntity resolveOwnedProject(final Integer projectId) {
        if (projectId == null) return null;
        final ProspectiveProjectEntity project = prospectiveProjectRepository.findById(projectId)
                .orElseThrow(() -> new javax.persistence.EntityNotFoundException("prospective.project.not-found"));
        if (!AuthUtils.isCreatedBy(project.getCreatedBy())) {
            log.warn("Prospective Keep project selection rejected: projectId={}, owner={}",
                    projectId, project.getCreatedBy());
            throw new NotAuthorizedException("common.result.access-not-authorized");
        }
        return project;
    }

    /** Keep과 검증된 Project의 nullable 관계를 저장하고 응답 DTO를 동기화한다. */
    private void applyProjectSelection(
            final ProspectiveKeepDto dto,
            final ProspectiveProjectEntity project
    ) throws Exception {
        final ProspectiveKeepEntity entity = this.getSelf().getDtlEntity(dto.getId());
        entity.setProject(project);
        repository.saveAndFlush(entity);
        dto.setProjectId(project == null ? null : project.getId());
    }

    /**
     * Keep의 다른 필드는 유지하고 단일 Project 소속만 변경한다.
     * null Project는 Project 미지정 상태를 뜻한다.
     */
    @Transactional
    public void moveProject(final Integer keepId, final Integer projectId) throws Exception {
        final ProspectiveKeepEntity keep = this.getSelf().getDtlEntity(keepId);
        if (!AuthUtils.isCreatedBy(keep.getCreatedBy())) {
            log.warn("Prospective Keep project move rejected: keepId={}, owner={}", keepId, keep.getCreatedBy());
            throw new NotAuthorizedException("common.result.access-not-authorized");
        }
        final ProspectiveProjectEntity project = this.resolveOwnedProject(projectId);
        keep.setProject(project);
        repository.saveAndFlush(keep);
        prospectiveKeepCacheEvictor.evictAfterCommit(keep.getCreatedBy(), keepId);
        log.info("Prospective Keep project moved: keepId={}, projectId={}, owner={}",
                keepId, projectId, keep.getCreatedBy());
    }

    /**
     * 선택한 말머리의 소유·활성 상태를 검증하고 prefix_content 연결을 반영한다.
     * <p>
     * attachable 정체성 키와 말머리 목록 Scope 모두 {@code PROSPECTIVE_KEEP} 를 쓴다.
     * 소유 검증은 등록·수정 전처리(createdBy)가 이미 끝낸 상태다.
     * </p>
     *
     * @param dto 저장된 Keep DTO
     * @param requestedPrefixId 요청한 말머리 ID (null 이면 선택 해제)
     */
    private void applyPrefixSelection(final ProspectiveKeepDto dto, final Integer requestedPrefixId) {
        final PrefixEntity prefix = prefixContentService.applySelection(
                new BaseAttachableKey(dto.getId(), ContentType.PROSPECTIVE_KEEP.key),
                ContentType.PROSPECTIVE_KEEP.key,
                requestedPrefixId
        );
        dto.setPrefix(prefix == null ? null : PrefixDto.displayOf(
                    prefix.getId(),
                    prefix.getName(),
                    prefix.getTextClass(),
                    prefix.getSortOrder(),
                    prefix.getActiveYn()
                ));
        dto.setPrefixId(requestedPrefixId);
    }

    /**
     * 등록 전처리. (override)
     *
     * @param registDto 등록할 객체
     */
    @Override
    public void preRegist(final ProspectiveKeepDto registDto) throws Exception {
        // 정렬 순서 처리 :: 월 무관 사용자 전역 순번 (활성 집합 cross-month 우선순위)
        final String username = AuthUtils.requireLoginUsername();
        final Integer lastSortOrder = repository.findLastIndexByCreatedBy(username).orElse(0);
        registDto.setSortOrder(lastSortOrder + 1);
    }

    /**
     * 수정 전처리. (override)
     *
     * @param modifyDto 수정할 객체 (dto)
     * @param modifyEntity 수정할 객체 (entity)
     */
    @Override
    public void preModify(final ProspectiveKeepDto modifyDto, final ProspectiveKeepEntity modifyEntity) throws Exception {
        if (!AuthUtils.isCreatedBy(modifyEntity.getCreatedBy())) {
            throw new NotAuthorizedException("common.result.access-not-authorized");
        }
    }

    /**
     * 등록 후처리. (override)
     *
     * @param updatedDto - 등록된 객체
     */
    @Override
    public void postRegist(final ProspectiveKeepDto updatedDto) throws Exception {
        // 관련 캐시 삭제
        prospectiveKeepCacheEvictor.evictAfterCommit(updatedDto.getCreatedBy(), updatedDto.getId());
    }

    /**
     * 수정 후처리. (override)
     *
     * @param updatedDto - 등록된 객체
     */
    @Override
    public void postModify(final ProspectiveKeepDto postDto, final ProspectiveKeepDto updatedDto) throws Exception {
        // 관련 캐시 삭제
        prospectiveKeepCacheEvictor.evictAfterCommit(updatedDto.getCreatedBy(), updatedDto.getId());
    }

    /**
     * 상세 조회 (dto level) :: 캐시 처리
     *
     * @param key 식별자
     * @return {@link ProspectiveKeepDto} -- 조회된 객체
     */
    @Cacheable(value="prospectiveKeepDetailDtoByUser", key="new org.springframework.cache.interceptor.SimpleKey(#username, #key)")
    public ProspectiveKeepDto getDetailDtoWithCacheByUser(final String username, final Integer key) throws Exception {
        final ProspectiveKeepEntity retrievedEntity = this.getSelf().getDtlEntity(key);
        final ProspectiveKeepDto retrieved = mapstruct.toDto(retrievedEntity);
        // 권한 체크
        if (!retrieved.getIsCreatedBy(AuthUtils.requireUsername(username))) throw new NotAuthorizedException("common.result.access-not-authorized");
        return retrieved;
    }

    /**
     * 상세 조회 (dto level) :: lifecycle 부착.
     * <p>
     * 본문은 상세 캐시({@link #getDetailDtoWithCacheByUser})를 그대로 쓰고, lifecycle 은 목록과 같은
     * {@link #applyKeepLifecycles}로 매 조회 신선하게 부착한다(행이 없으면 {@code OPEN}).
     * lifecycle 변경은 Keep 상세 캐시를 비우지 않으므로 캐시 안에 넣지 않으며,
     * 캐시된 인스턴스를 바꾸지 않도록 얕은 복사본에 부착한다.
     * </p>
     *
     * @param username 사용자 계정명
     * @param key 식별자
     * @return {@link ProspectiveKeepDto} -- 조회된 객체 (lifecycle 부착)
     */
    public ProspectiveKeepDto getDetailDtoWithLifecycleByUser(final String username, final Integer key) throws Exception {
        final ProspectiveKeepDto cached = this.getSelf().getDetailDtoWithCacheByUser(username, key);
        final ProspectiveKeepDto retrieved = cached.toBuilder().build();
        this.applyKeepLifecycles(List.of(retrieved));
        return retrieved;
    }

    /**
     * 삭제 전처리. (override)
     *
     * @param deletedDto - 삭제된 객체
     */
    @Override
    public void preDelete(final ProspectiveKeepDto deletedDto) throws Exception {
        if (!AuthUtils.isCreatedBy(deletedDto.getCreatedBy())) {
            throw new NotAuthorizedException("common.result.access-not-authorized");
        }
    }

    /**
     * 삭제 후처리. (override)
     *
     * @param deletedDto - 삭제된 객체
     */
    @Override
    public void postDelete(final ProspectiveKeepDto deletedDto) throws Exception {
        // 관련 캐시 삭제
        prospectiveKeepCacheEvictor.evictAfterCommit(deletedDto.getCreatedBy(), deletedDto.getId());
    }

    /**
     * 종결(RESOLVED·EXPIRED·DROPPED) 상태인지 판정한다.
     * 라이프사이클 행이 없으면 OPEN 이므로 진행으로 본다.
     *
     * @param dto 대상 Keep DTO
     * @return 종결이면 true
     */
    private boolean isTerminal(final ProspectiveKeepDto dto) {
        if (dto.getLifecycle() == null) return false;
        final LifecycleKey key = LifecycleKey.getByKey(dto.getLifecycle().getLifecycleKey());
        return key != null && key.isTerminal();
    }

    /**
     * Keep DTO 목록에 라이프사이클을 부착한다.
     * <p>
     * 부착 테이블에 행이 없으면 {@code OPEN}. 스레드·엔트리 enrich 와 동일 계약이다.
     * </p>
     *
     * @param dtoList 대상 Keep DTO 목록
     */
    private void applyKeepLifecycles(final List<ProspectiveKeepDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) return;
        final List<Integer> keepIds = dtoList.stream()
                .map(ProspectiveKeepDto::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (keepIds.isEmpty()) return;
        final java.util.Map<Integer, LifecycleCmpstn> lifecycleMap =
                lifecycleService.getKeepLifecycleMap(keepIds);
        for (final ProspectiveKeepDto dto : dtoList) {
            if (dto == null || dto.getId() == null) continue;
            dto.setLifecycle(lifecycleMap.getOrDefault(dto.getId(), LifecycleCmpstn.open()));
        }
    }
}
