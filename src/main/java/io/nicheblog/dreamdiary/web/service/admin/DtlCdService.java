package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCdDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.mapstruct.admin.DtlCdMapstruct;
import io.nicheblog.dreamdiary.web.repository.admin.jpa.DtlCdRepository;
import io.nicheblog.dreamdiary.web.spec.admin.DtlCdSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;

/**
 * DtlCdService
 * <pre>
 *  상세코드 관리 서비스 모듈
 *  ※상세코드(dtl_cd) = 분류코드 하위의 상세코드. 분류코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseManageService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("dtlCdService")
@RequiredArgsConstructor
public class DtlCdService
        implements BaseCrudService<DtlCdDto, DtlCdDto, DtlCdKey, DtlCdEntity, DtlCdRepository, DtlCdSpec, DtlCdMapstruct>,
                   BaseStateService<DtlCdDto, DtlCdDto, DtlCdKey, DtlCdEntity, DtlCdRepository, DtlCdSpec, DtlCdMapstruct> {

    private final DtlCdRepository dtlCdRepository;
    private final DtlCdSpec dtlCdSpec;
    private final DtlCdMapstruct dtlCdMapstruct = DtlCdMapstruct.INSTANCE;

    @Override
    public DtlCdRepository getRepository() {
        return this.dtlCdRepository;
    }
    @Override
    public DtlCdSpec getSpec() {
        return this.dtlCdSpec;
    }
    @Override
    public DtlCdMapstruct getMapstruct() {
        return this.dtlCdMapstruct;
    }

    /**
     * 등록 전처리 :: override
     */
    @Override
    public void preRegist(final DtlCdDto dtlCd) {
        if (dtlCd.getState() == null) dtlCd.setState(new StateCmpstn());
    }

    /**
     * 등록 후처리 :: override
     */
    @Override
    public void postRegist(final DtlCdEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 수정 후처리 :: override
     */
    @Override
    public void postModify(final DtlCdEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 삭제 후처리 :: override
     */
    @Override
    public void postDelete(final DtlCdEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 관련 캐시 처리 :: 메소드 분리
     */
    public void evictRelatedCache(final DtlCdEntity rslt) {
        EhCacheUtils.evictCache("cdEntityListByClCd", rslt.getClCd());
        EhCacheUtils.evictCache("cdDtoListByClCd", rslt.getClCd());
        // 연관된 모든 엔티티의 캐시 클리어
        EhCacheUtils.clearL2Cache();
    }

    /**
     * 정렬 순서 업데이트
     */
    @Transactional
    public boolean sortOrdr(List<DtlCdDto> sortOrdr) throws Exception {
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