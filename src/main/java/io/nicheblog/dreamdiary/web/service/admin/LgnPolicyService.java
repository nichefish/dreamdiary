package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.LgnPolicyMapstruct;
import io.nicheblog.dreamdiary.web.model.admin.LgnPolicyDto;
import io.nicheblog.dreamdiary.web.repository.admin.LgnPolicyRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public LgnPolicyDto getLgnPolicyDtlDto() throws Exception {
        // entity level
        LgnPolicyEntity rsLgnPolicyEntity = this.getLgnPolicyDtlEntity();
        // Entity -> Dto
        return (rsLgnPolicyEntity != null) ? lgnPolicyMapstruct.toDto(rsLgnPolicyEntity) : null;
    }

    /**
     * 사용자 관리 > 로그인 설정 조회 (Entity 레벨)
     * Cacheable (key:: default)
     */
    @Cacheable(value = "lgnPolicyEntity")   // 항목이 한 개 고정일 경우 Cache의 key는 불필요하다.
    public LgnPolicyEntity getLgnPolicyDtlEntity() throws Exception {
        Optional<LgnPolicyEntity> rsLgnPolicyEntityWrapper = lgnPolicyRepository.findById(1);
        return rsLgnPolicyEntityWrapper.orElse(null);
    }

    /**
     * 사용자 관리 > 로그인 설정 정보 등록/수정
     * Clears Cache (allEnties)
     */
    @CacheEvict(value = {"lgnPolicyEntity", "lgnPolicy"}, allEntries = true)
    public Boolean lgnPolicyReg(final LgnPolicyDto LgnPolicyDto) throws Exception {
        // Dto -> Entity
        LgnPolicyEntity LgnPolicyEntity = lgnPolicyMapstruct.toEntity(LgnPolicyDto);
        // insert/update
        Integer rsId = lgnPolicyRepository.save(LgnPolicyEntity)
                                          .getLgnPolicyNo();
        return (rsId != null);
    }
}
