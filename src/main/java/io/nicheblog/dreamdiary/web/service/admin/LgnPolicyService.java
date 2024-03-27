package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.LgnPolicyMapstruct;
import io.nicheblog.dreamdiary.web.model.admin.LgnPolicyDto;
import io.nicheblog.dreamdiary.web.repository.admin.LgnPolicyRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * LgnPolicyService
 * <pre>
 *  로그인 정책 정보 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("lgnPolicyService")
public class LgnPolicyService {

    private final LgnPolicyMapstruct lgnPolicyMapstruct = LgnPolicyMapstruct.INSTANCE;

    @Resource(name = "lgnPolicyRepository")
    private LgnPolicyRepository lgnPolicyRepository;

    /**
     * 사용자 관리 > 로그인 설정 조회 (Dto 레벨)
     * Cacheable (key:: default)
     */
    @Cacheable(value = "lgnPolicy")   // 항목이 한 개 고정일 경우 Cache의 key는 불필요하다.
    public LgnPolicyDto getDtlDto() throws Exception {
        // entity level
        LgnPolicyEntity rsLgnPolicyEntity = this.getDtlEntity();
        // Entity -> Dto
        return (rsLgnPolicyEntity != null) ? lgnPolicyMapstruct.toDto(rsLgnPolicyEntity) : null;
    }

    /**
     * 사용자 관리 > 로그인 설정 조회 (Entity 레벨)
     * Cacheable (key:: default)
     */
    @Cacheable(value = "lgnPolicyEntity")   // 항목이 한 개 고정일 경우 Cache의 key는 불필요하다.
    public LgnPolicyEntity getDtlEntity() throws Exception {
        Optional<LgnPolicyEntity> rsWrapper = lgnPolicyRepository.findById(1);
        if (rsWrapper.isEmpty()) {
            List<LgnPolicyEntity> scrapAll = lgnPolicyRepository.findAll();
            if (CollectionUtils.isEmpty(scrapAll)) return null;
            return scrapAll.get(0);
        }
        return rsWrapper.orElse(null);
    }

    /**
     * 사용자 관리 > 로그인 설정 정보 등록/수정
     * Clears Cache (allEnties)
     */
    @CacheEvict(value = {"lgnPolicyEntity", "lgnPolicy"}, allEntries = true)
    public Boolean regist(final LgnPolicyDto LgnPolicyDto) throws Exception {
        // Dto -> Entity
        LgnPolicyEntity lgnPolicy = lgnPolicyMapstruct.toEntity(LgnPolicyDto);
        // insert/update
        Integer rsId = lgnPolicyRepository.save(lgnPolicy)
                                          .getLgnPolicyNo();
        return (rsId != null);
    }
}
