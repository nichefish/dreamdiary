package io.nicheblog.dreamdiary.domain.admin.lgnPolicy.service;

import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.entity.LgnPolicyEntity;
import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.mapstruct.LgnPolicyMapstruct;
import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.model.LgnPolicyDto;
import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.repository.jpa.LgnPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * LgnPolicyService
 * <pre>
 *  로그인 정책 정보 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("lgnPolicyService")
@RequiredArgsConstructor
public class LgnPolicyService {

    private final LgnPolicyRepository repository;
    private final LgnPolicyMapstruct mapstruct = LgnPolicyMapstruct.INSTANCE;

    /**
     * 사용자 관리 > 로그인 설정 조회 (Dto 레벨)
     *
     * @return {@link LgnPolicyDto} -- 로그인 설정 정보
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Cacheable(value = "lgnPolicy")   // 항목이 한 개 고정일 경우 Cache의 key는 불필요하다.
    public LgnPolicyDto getDtlDto() throws Exception {
        // entity level
        final LgnPolicyEntity rsLgnPolicyEntity = this.getDtlEntity();
        // Entity -> Dto 변환
        return (rsLgnPolicyEntity != null) ? mapstruct.toDto(rsLgnPolicyEntity) : null;
    }

    /**
     * 사용자 관리 > 로그인 설정 조회 (Entity 레벨)
     *
     * @return {@link LgnPolicyEntity} -- 로그인 설정 엔티티
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Cacheable(value = "lgnPolicyEntity")   // 항목이 한 개 고정일 경우 Cache의 key는 불필요하다.
    public LgnPolicyEntity getDtlEntity() throws Exception {
        final Optional<LgnPolicyEntity> rsWrapper = repository.findById(1);
        if (rsWrapper.isEmpty()) {
            final List<LgnPolicyEntity> scrapAll = repository.findAll();
            if (CollectionUtils.isEmpty(scrapAll)) return null;
            return scrapAll.get(0);
        }
        return rsWrapper.orElse(null);
    }

    /**
     * 사용자 관리 > 로그인 설정 정보 등록/수정
     *
     * @param LgnPolicyDto 로그인 정책 정보 Dto
     * @return {@link Boolean} -- 성공 여부를 나타내는 Boolean 값
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @CacheEvict(value = {"lgnPolicyEntity", "lgnPolicy"}, allEntries = true)
    public Boolean regist(final LgnPolicyDto LgnPolicyDto) throws Exception {
        // Dto -> Entity 변환
        final LgnPolicyEntity lgnPolicy = mapstruct.toEntity(LgnPolicyDto);
        // insert/update
        final Integer rsId = repository.save(lgnPolicy).getLgnPolicyNo();
        return (rsId != null);
    }
}
