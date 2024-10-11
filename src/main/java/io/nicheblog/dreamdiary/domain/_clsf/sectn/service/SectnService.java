package io.nicheblog.dreamdiary.domain._clsf.sectn.service;

import io.nicheblog.dreamdiary.domain._clsf.sectn.entity.SectnEntity;
import io.nicheblog.dreamdiary.domain._clsf.sectn.mapstruct.SectnMapstruct;
import io.nicheblog.dreamdiary.domain._clsf.sectn.model.SectnDto;
import io.nicheblog.dreamdiary.domain._core.cache.service.EhCacheEvictService;
import io.nicheblog.dreamdiary.domain._clsf.sectn.repository.jpa.SectnRepository;
import io.nicheblog.dreamdiary.domain._clsf.sectn.spec.SectnSpec;
import io.nicheblog.dreamdiary.domain._clsf.state.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;

/**
 * SectnService
 * <pre>
 *  단락 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("sectnService")
@RequiredArgsConstructor
@Log4j2
public class SectnService
        implements BaseMultiCrudService<SectnDto, SectnDto, Integer, SectnEntity, SectnRepository, SectnSpec, SectnMapstruct> {

    @Getter
    private final SectnRepository repository;
    @Getter
    private final SectnSpec spec;
    @Getter
    private final SectnMapstruct mapstruct = SectnMapstruct.INSTANCE;

    private final EhCacheEvictService ehCacheEvictService;

    /**
     * 등록 전처리. (override)
     *
     * @param dto 등록할 객체
     */
    @Override
    public void preRegist(final SectnDto dto) {
        if (dto.getState() == null) dto.setState(new StateCmpstn());
    }

    /**
     * 등록 후처리. (override)
     *
     * @param rslt - 등록된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postRegist(final SectnEntity rslt) throws Exception {
        // 관련 캐시 삭제
        this.evictClsfCache(rslt);
    }

    /**
     * 수정 후처리. (override)
     *
     * @param rslt - 수정된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postModify(final SectnEntity rslt) throws Exception {
        // 관련 캐시 삭제
        this.evictClsfCache(rslt);
    }

    /**
     * 삭제 후처리. (override)
     *
     * @param rslt - 삭제된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postDelete(final SectnEntity rslt) throws Exception {
        // 관련 캐시 삭제
        this.evictClsfCache(rslt);

        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 관련 캐시 삭제.
     *
     * @param rslt 캐시 처리할 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public void evictClsfCache(final SectnEntity rslt) throws Exception {
        String refContentType = rslt.getRefContentType();
        Integer refPostNo = rslt.getRefPostNo();
        ehCacheEvictService.evictClsfCache(refContentType, refPostNo);
        EhCacheUtils.clearL2Cache(SectnEntity.class);
    }

    /**
     * 정렬 순서 업데이트.
     *
     * @param sortOrdr 키 + 정렬 순서로 이루어진 목록
     * @return {@link Boolean} -- 성공시 true 반환
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public boolean sortOrdr(final List<SectnDto> sortOrdr) throws Exception {
        if (CollectionUtils.isEmpty(sortOrdr)) return true;
        sortOrdr.forEach(dto -> {
            try {
                SectnEntity e = this.getDtlEntity(dto.getKey());
                e.getState().setSortOrdr(dto.getState().getSortOrdr());
                this.updt(e);
            } catch (Exception ex) {
                ex.getStackTrace();
                // 로그 기록, 예외 처리 등
                throw new RuntimeException(ex);
            }
        });
        return true;
    }
}