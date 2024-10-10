package io.nicheblog.dreamdiary.domain._core.cd.service;

import io.nicheblog.dreamdiary.domain._core.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.domain._core.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.domain._core.cd.mapstruct.DtlCdMapstruct;
import io.nicheblog.dreamdiary.domain._core.cd.model.DtlCdDto;
import io.nicheblog.dreamdiary.domain._core.cd.repository.jpa.DtlCdRepository;
import io.nicheblog.dreamdiary.domain._core.cd.spec.DtlCdSpec;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;

/**
 * DtlCdService
 * <pre>
 *  상세 코드 관리 서비스 모듈
 *  ※상세 코드(dtl_cd) = 분류 코드 하위의 상세 코드. 분류 코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseManageService
 */
@Service("dtlCdService")
@RequiredArgsConstructor
public class DtlCdService
        implements BaseCrudService<DtlCdDto, DtlCdDto, DtlCdKey, DtlCdEntity, DtlCdRepository, DtlCdSpec, DtlCdMapstruct>,
                   BaseStateService<DtlCdDto, DtlCdDto, DtlCdKey, DtlCdEntity, DtlCdRepository, DtlCdSpec, DtlCdMapstruct> {

    @Getter
    private final DtlCdRepository repository;
    @Getter
    private final DtlCdSpec spec;
    @Getter
    private final DtlCdMapstruct mapstruct = DtlCdMapstruct.INSTANCE;

    /**
     * 등록 전처리. (override)
     *
     * @param dto 등록할 객체
     */
    @Override
    public void preRegist(final DtlCdDto dto) {
        if (dto.getState() == null) dto.setState(new StateCmpstn());
    }

    /**
     * 등록 후처리. (override)
     *
     * @param rslt - 등록된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postRegist(final DtlCdEntity rslt) throws Exception {
        // 관련 캐시 삭제
        this.evictRelatedCache(rslt);
    }

    /**
     * 수정 후처리. (override)
     *
     * @param rslt - 수정된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postModify(final DtlCdEntity rslt) throws Exception {
        // 관련 캐시 삭제
        this.evictRelatedCache(rslt);
    }

    /**
     * 삭제 후처리. (override)
     *
     * @param rslt - 삭제된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postDelete(final DtlCdEntity rslt) throws Exception {
        // 관련 캐시 삭제
        this.evictRelatedCache(rslt);
    }

    /**
     * 관련 캐시 삭제.
     *
     * @param rslt 캐시 처리할 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public void evictRelatedCache(final DtlCdEntity rslt) {
        EhCacheUtils.evictCache("cdEntityListByClCd", rslt.getClCd());
        EhCacheUtils.evictCache("cdDtoListByClCd", rslt.getClCd());
        // 연관된 모든 엔티티의 캐시 클리어
        EhCacheUtils.clearL2Cache();
    }

    /**
     * 정렬 순서 업데이트.
     *
     * @param sortOrdr 키 + 정렬 순서로 이루어진 목록
     * @return {@link Boolean} -- 성공시 true 반환
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public boolean sortOrdr(final List<DtlCdDto> sortOrdr) throws Exception {
        if (CollectionUtils.isEmpty(sortOrdr)) return true;
        sortOrdr.forEach(dto -> {
            try {
                DtlCdEntity e = this.getDtlEntity(dto.getKey());
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