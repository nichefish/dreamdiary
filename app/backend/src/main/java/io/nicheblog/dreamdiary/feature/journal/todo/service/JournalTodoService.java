package io.nicheblog.dreamdiary.feature.journal.todo.service;

import io.nicheblog.dreamdiary.auth.security.exception.NotAuthorizedException;
import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.feature.attachable._shared.entity.BaseAttachableKey;
import io.nicheblog.dreamdiary.feature.attachable._shared.service.BaseAttachableService;
import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.PrefixEntity;
import io.nicheblog.dreamdiary.feature.attachable.prefix.model.PrefixDto;
import io.nicheblog.dreamdiary.feature.attachable.prefix.service.PrefixContentService;
import io.nicheblog.dreamdiary.feature.journal._shared.handler.JournalCacheEvictWorker;
import io.nicheblog.dreamdiary.feature.journal._shared.model.JournalCacheEvictParam;
import io.nicheblog.dreamdiary.feature.journal.todo.entity.JournalTodoEntity;
import io.nicheblog.dreamdiary.feature.journal.todo.mapstruct.JournalTodoMapstruct;
import io.nicheblog.dreamdiary.feature.journal.todo.model.JournalTodoDto;
import io.nicheblog.dreamdiary.feature.journal.todo.model.JournalTodoSearchParam;
import io.nicheblog.dreamdiary.feature.journal.todo.repository.jpa.JournalTodoRepository;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.LifecycleKey;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.service.LifecycleService;
import io.nicheblog.dreamdiary.feature.journal._shared.lifecycle.JournalLifecycleViewHelper;
import io.nicheblog.dreamdiary.feature.journal.todo.spec.JournalTodoSpec;
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
 * JournalTodoService
 * <pre>
 *  저널 일기 관리 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class JournalTodoService
        implements BaseAttachableService<JournalTodoDto, JournalTodoDto, Integer, JournalTodoEntity> {

    @Getter
    private final JournalTodoRepository repository;
    @Getter
    private final JournalTodoSpec spec;
    @Getter
    private final JournalTodoMapstruct mapstruct = JournalTodoMapstruct.INSTANCE;

    public JournalTodoMapstruct getReadMapstruct() {
        return this.mapstruct;
    }
    public JournalTodoMapstruct getWriteMapstruct() {
        return this.mapstruct;
    }

    private final JournalCacheEvictWorker journalCacheEvictWorker;
    private final LifecycleService lifecycleService;
    private final PrefixContentService prefixContentService;

    private final ApplicationContext context;
    private JournalTodoService getSelf() {
        return context.getBean(this.getClass());
    }

    /**
     * 활성 할일 목록 조회 (dto level).
     * <p>
     * 월에 종속되지 않고 사용자의 활성 집합(OPEN·PENDING)을 시점 무관하게 투영한다.
     * RESOLVED 는 활성 투영에서 제외한다. 소규모 개인 데이터라 리스트 캐시 없이 매 조회
     * 신선하게 lifecycle 을 부착한다.
     * (RESOLVED 제외는 인메모리 필터다. 카디널리티가 커지면 lifecycle 조인으로 쿼리 단계로 내린다.)
     * </p>
     *
     * @param username 사용자 계정명
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 활성 할일 목록
     */
    public List<JournalTodoDto> getActiveListDtoByUser(final String username, final JournalTodoSearchParam searchParam) throws Exception {
        return this.getListDtoWithLifecycleByUser(username, searchParam).stream()
                .filter(dto -> !isTerminal(dto))
                .collect(Collectors.toList());
    }

    /**
     * 사용자 전 범위 할일 목록 조회 (dto level).
     * <p>
     * 시점 무관하게 사용자의 모든 할일을 lifecycle 부착해 반환한다. 활성 집합과 달리
     * RESOLVED 를 제외하지 않는다 -- 전수관리(keep) 화면이 완료·미해결을 함께 투영하기 위함이다.
     * 소규모 개인 데이터라 리스트 캐시 없이 매 조회 신선하게 lifecycle 을 부착한다.
     * </p>
     *
     * @param username 사용자 계정명
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 사용자 전 범위 할일 목록 (lifecycle 부착)
     */
    public List<JournalTodoDto> getListDtoWithLifecycleByUser(final String username, final JournalTodoSearchParam searchParam) throws Exception {
        searchParam.setCreatedBy(AuthUtils.requireUsername(username));

        final List<JournalTodoDto> listDto = this.getSelf().getListDto(searchParam);
        this.applyTodoLifecycles(listDto);
        return listDto;
    }

    /**
     * 할일 본문과 개인 말머리 선택을 같은 트랜잭션에서 등록한다.
     *
     * @param registDto 등록할 객체
     * @return {@link ServiceResponse} -- 등록 결과 객체
     */
    @Override
    @Transactional
    public ServiceResponse regist(final JournalTodoDto registDto) throws Exception {
        final ServiceResponse response = BaseAttachableService.super.regist(registDto);
        final JournalTodoDto updatedDto = (JournalTodoDto) response.getRsltObj();
        this.applyPrefixSelection(updatedDto, registDto.getPrefixId());
        return response;
    }

    /**
     * 할일 본문과 개인 말머리 선택을 같은 트랜잭션에서 수정한다.
     *
     * @param modifyDto 수정할 객체
     * @return {@link ServiceResponse} -- 수정 결과 객체
     */
    @Override
    @Transactional
    public ServiceResponse modify(final JournalTodoDto modifyDto) throws Exception {
        final ServiceResponse response = BaseAttachableService.super.modify(modifyDto);
        final JournalTodoDto updatedDto = (JournalTodoDto) response.getRsltObj();
        this.applyPrefixSelection(updatedDto, modifyDto.getPrefixId());
        return response;
    }

    /**
     * 선택한 말머리의 소유·활성 상태를 검증하고 prefix_content 연결을 반영한다.
     * <p>
     * attachable 정체성 키와 말머리 목록 Scope 모두 {@code JOURNAL_TODO} 를 쓴다.
     * 소유 검증은 등록·수정 전처리(createdBy)가 이미 끝낸 상태다.
     * </p>
     *
     * @param dto 저장된 할일 DTO
     * @param requestedPrefixId 요청한 말머리 ID (null 이면 선택 해제)
     */
    private void applyPrefixSelection(final JournalTodoDto dto, final Integer requestedPrefixId) {
        final PrefixEntity prefix = prefixContentService.applySelection(
                new BaseAttachableKey(dto.getId(), ContentType.JOURNAL_TODO.key),
                ContentType.JOURNAL_TODO.key,
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
    public void preRegist(final JournalTodoDto registDto) throws Exception {
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
    public void preModify(final JournalTodoDto modifyDto, final JournalTodoEntity modifyEntity) throws Exception {
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
    public void postRegist(final JournalTodoDto updatedDto) throws Exception {
        // 관련 캐시 삭제
        journalCacheEvictWorker.evictAfterCommit(JournalCacheEvictParam.of(updatedDto), ContentType.JOURNAL_TODO);
    }

    /**
     * 수정 후처리. (override)
     *
     * @param updatedDto - 등록된 객체
     */
    @Override
    public void postModify(final JournalTodoDto postDto, final JournalTodoDto updatedDto) throws Exception {
        // 관련 캐시 삭제
        journalCacheEvictWorker.evictAfterCommit(JournalCacheEvictParam.of(updatedDto), ContentType.JOURNAL_TODO);
    }

    /**
     * 상세 조회 (dto level) :: 캐시 처리
     *
     * @param key 식별자
     * @return {@link JournalTodoDto} -- 조회된 객체
     */
    @Cacheable(value="journalTodoDetailDtoByUser", key="new org.springframework.cache.interceptor.SimpleKey(#username, #key)")
    public JournalTodoDto getDetailDtoWithCacheByUser(final String username, final Integer key) throws Exception {
        final JournalTodoEntity retrievedEntity = this.getSelf().getDtlEntity(key);
        final JournalTodoDto retrieved = mapstruct.toDto(retrievedEntity);
        // 권한 체크
        if (!retrieved.getIsCreatedBy(AuthUtils.requireUsername(username))) throw new NotAuthorizedException("common.result.access-not-authorized");
        return retrieved;
    }

    /**
     * 삭제 전처리. (override)
     *
     * @param deletedDto - 삭제된 객체
     */
    @Override
    public void preDelete(final JournalTodoDto deletedDto) throws Exception {
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
    public void postDelete(final JournalTodoDto deletedDto) throws Exception {
        // 관련 캐시 삭제
        journalCacheEvictWorker.evictAfterCommit(JournalCacheEvictParam.of(deletedDto), ContentType.JOURNAL_TODO);
    }

    /**
     * 종결(RESOLVED·EXPIRED·DROPPED) 상태인지 판정한다.
     * 라이프사이클 행이 없으면 OPEN 이므로 진행으로 본다.
     *
     * @param dto 대상 할일 DTO
     * @return 종결이면 true
     */
    private boolean isTerminal(final JournalTodoDto dto) {
        if (dto.getLifecycle() == null) return false;
        final LifecycleKey key = LifecycleKey.getByKey(dto.getLifecycle().getLifecycleKey());
        return key != null && key.isTerminal();
    }

    /**
     * 할일 DTO 목록에 라이프사이클을 부착한다.
     * <p>
     * 부착 테이블에 행이 없으면 {@code OPEN}. 스레드·엔트리 enrich 와 동일 계약이다.
     * </p>
     *
     * @param dtoList 대상 할일 DTO 목록
     */
    private void applyTodoLifecycles(final List<JournalTodoDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) return;
        final List<Integer> todoIds = dtoList.stream()
                .map(JournalTodoDto::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (todoIds.isEmpty()) return;
        JournalLifecycleViewHelper.applyTodoLifecycle(
                dtoList,
                lifecycleService.getLifecycleMap(ContentType.JOURNAL_TODO, todoIds)
        );
    }
}

