package io.nicheblog.dreamdiary.web.service.jrnl.sumry;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryCnEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.sumry.JrnlSumryCnMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.sumry.JrnlSumryCnDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.sumry.jpa.JrnlSumryCnRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.sumry.JrnlSumryCnSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * JrnlSumryCnService
 * <pre>
 *  저널 결산 내용 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseClsfService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlSumryCnService")
@Log4j2
public class JrnlSumryCnService
        implements BaseMultiCrudService<JrnlSumryCnDto, JrnlSumryCnDto, Integer, JrnlSumryCnEntity, JrnlSumryCnRepository, JrnlSumryCnSpec, JrnlSumryCnMapstruct> {

    private final JrnlSumryCnMapstruct jrnlSumryCnMapstruct = JrnlSumryCnMapstruct.INSTANCE;

    @Resource(name = "jrnlSumryCnRepository")
    private JrnlSumryCnRepository jrnlSumryCnRepository;
    @Resource(name = "jrnlSumryCnSpec")
    private JrnlSumryCnSpec jrnlSumryCnSpec;

    @Override
    public JrnlSumryCnRepository getRepository() {
        return this.jrnlSumryCnRepository;
    }
    @Override
    public JrnlSumryCnMapstruct getMapstruct() {
        return this.jrnlSumryCnMapstruct;
    }
    @Override
    public JrnlSumryCnSpec getSpec() {
        return this.jrnlSumryCnSpec;
    }

    /**
     * 등록 전처리 :: override
     */
    @Override
    public void preRegist(final JrnlSumryCnDto jrnlSumryCn) {
        // 인덱스(정렬순서) 처리
        Integer jrnlSumryNo = jrnlSumryCn.getJrnlSumryNo();
        String jrnlSumryTyCd = jrnlSumryCn.getCtgrCd();
        Integer lastIndex = jrnlSumryCnRepository.findLastIndexByJrnlSumryAndSumryTy(jrnlSumryNo, jrnlSumryTyCd).orElse(0);
        jrnlSumryCn.setIdx(lastIndex + 1);
    }

    /**
     * 등록 후처리 :: override
     */
    @Override
    public void postRegist(final JrnlSumryCnEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 상세 조회 (dto level) :: 캐시 처리
     */
    @Cacheable(value="jrnlSumryCnDtlDto", key="#key")
    public JrnlSumryCnDto getDtlDtoWithCache(Integer key) throws Exception {
        return this.getDtlDto(key);
    }

    /**
     * 수정 후처리 :: override
     */
    @Override
    public void postModify(final JrnlSumryCnEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 삭제 후처리 :: override
     */
    @Override
    public void postDelete(final JrnlSumryCnEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);

        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 관련 캐시 처리 :: 메소드 분리
     */
    public void evictRelatedCache(final JrnlSumryCnEntity rslt) {
        // jrnl_sumry
        EhCacheUtils.evictCache("jrnlSumryDtl", rslt.getJrnlSumryNo());
        EhCacheUtils.evictCache("jrnlSumryDtlByYy", rslt.getJrnlSumry().getYy());
        // jrnl_sumry_cn
        EhCacheUtils.evictCache("jrnlSumryCnDtlDto", rslt.getPostNo());
    }
}