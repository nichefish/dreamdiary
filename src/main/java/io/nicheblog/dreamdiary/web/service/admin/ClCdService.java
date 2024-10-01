package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCdDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.mapstruct.admin.ClCdMapstruct;
import io.nicheblog.dreamdiary.web.repository.admin.jpa.ClCdRepository;
import io.nicheblog.dreamdiary.web.spec.admin.ClCdSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;

/**
 * ClCdService
 * <pre>
 *  분류코드 관리 서비스 모듈
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseManageService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("clCdService")
@RequiredArgsConstructor
public class ClCdService
        implements BaseCrudService<ClCdDto, ClCdDto, String, ClCdEntity, ClCdRepository, ClCdSpec, ClCdMapstruct>,
                   BaseStateService<ClCdDto, ClCdDto, String, ClCdEntity, ClCdRepository, ClCdSpec, ClCdMapstruct> {

    private final ClCdRepository clCdRepository;
    private final ClCdSpec clCdSpec;
    private final ClCdMapstruct clCdMapstruct = ClCdMapstruct.INSTANCE;

    @Override
    public ClCdRepository getRepository() {
        return this.clCdRepository;
    }
    @Override
    public ClCdSpec getSpec() {
        return this.clCdSpec;
    }
    @Override
    public ClCdMapstruct getMapstruct() {
        return this.clCdMapstruct;
    }

    /**
     * 등록 전처리 :: override
     */
    @Override
    public void preRegist(final ClCdDto clCd) {
        if (clCd.getState() == null) clCd.setState(new StateCmpstn());
    }

    /**
     * 등록 후처리 :: override
     */
    @Override
    public void postRegist(final ClCdEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 수정 후처리 :: override
     */
    @Override
    public void postModify(final ClCdEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 삭제 후처리 :: override
     */
    @Override
    public void postDelete(final ClCdEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 관련 캐시 처리 :: 메소드 분리
     */
    public void evictRelatedCache(final ClCdEntity rslt) {
        EhCacheUtils.evictCache("cdEntityListByClCd", rslt.getClCd());
        EhCacheUtils.evictCache("cdDtoListByClCd", rslt.getClCd());
        // 연관된 모든 엔티티의 캐시 클리어
        EhCacheUtils.clearL2Cache();
    }

    /**
     * 정렬 순서 업데이트
     */
    @Transactional
    public boolean sortOrdr(final List<ClCdDto> sortOrdr) throws Exception {
        if (CollectionUtils.isEmpty(sortOrdr)) return true;
        sortOrdr.forEach(dto -> {
            try {
                ClCdEntity e = this.getDtlEntity(dto.getClCd());
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