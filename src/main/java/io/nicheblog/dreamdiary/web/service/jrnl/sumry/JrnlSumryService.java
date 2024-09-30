package io.nicheblog.dreamdiary.web.service.jrnl.sumry;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.sumry.JrnlSumryMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.sumry.JrnlSumryDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.sumry.jpa.JrnlSumryRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.sumry.JrnlSumrySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JrnlSumryService
 * <pre>
 *  저널 결산 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlSumryService")
@RequiredArgsConstructor
@Log4j2
public class JrnlSumryService
        implements BaseReadonlyService<JrnlSumryDto.DTL, JrnlSumryDto.LIST, Integer, JrnlSumryEntity, JrnlSumryRepository, JrnlSumrySpec, JrnlSumryMapstruct> {

    private final JrnlSumryRepository jrnlSumryRepository;
    private final JrnlSumrySpec jrnlSumrySpec;
    private final JrnlSumryMapstruct jrnlSumryMapstruct = JrnlSumryMapstruct.INSTANCE;

    @Override
    public JrnlSumryRepository getRepository() {
        return this.jrnlSumryRepository;
    }
    @Override
    public JrnlSumryMapstruct getMapstruct() {
        return this.jrnlSumryMapstruct;
    }
    @Override
    public JrnlSumrySpec getSpec() {
        return this.jrnlSumrySpec;
    }

    /** 캐시 사용 위해 구현체로 pullUp */
    @Override
    @Cacheable(value="jrnlSumryList")
    public List<JrnlSumryDto.LIST> getListDto(final BaseSearchParam searchParam) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getListDto(searchParamMap);
    }

    /**
     * 년도를 받아서 해당 년도 결산 생성
     */
    @Caching(evict = {
            @CacheEvict(value={"jrnlTotalSumry", "jrnlSumryList"}, allEntries = true),
            @CacheEvict(value="jrnlSumryDtlByYy", key="#rslt.getYy()")
    })
    public Boolean makeYySumry(Integer yy) {
        // 해당 년도 결산 정보 조회
        JrnlSumryEntity sumry = jrnlSumryRepository.findByYy(yy).orElse(new JrnlSumryEntity(yy));

        // 해당 년도 꿈 일자 조회해서 갱신
        Integer dreamDayCntByYy = jrnlSumryRepository.getDreamDayCntByYy(yy);
        sumry.setDreamDayCnt(dreamDayCntByYy);
        // 해당 년도 꿈 조회해서 갱신
        Integer dreamCntByYy = jrnlSumryRepository.getDreamCntByYy(yy);
        sumry.setDreamCnt(dreamCntByYy);
        // 해당 년도 일기 일자 조회해서 갱신
        Integer diaryCntByYy = jrnlSumryRepository.getDiaryDayCntByYy(yy);
        sumry.setDiaryDayCnt(diaryCntByYy);

        jrnlSumryRepository.save(sumry);

        return true;
    }

    /**
     * 전체 년도에 대한 결산 생성
     */
    @CacheEvict(value={"jrnlTotalSumry", "jrnlSumryList", "jrnlSumryDtl"}, allEntries = true)
    public Boolean makeTotalYySumry() throws Exception {
        int currYy = DateUtils.getCurrYy();
        int startYy = 2011;
        for (int yy = startYy; yy <= currYy; yy++) {
            try {
                this.makeYySumry(yy);
            } catch (Exception e) {
                log.warn("Error creating annual summary for {}", yy);
            }
        }

        return true;
    }

    /**
     * 결산 정보를 취합해서 총 결산 생성
     */
    @Cacheable(value="jrnlTotalSumry")
    public JrnlSumryDto getTotalSumry() {
        JrnlSumryDto totalSumry = new JrnlSumryDto();
        // 해당 년도 꿈 일자 조회해서 갱신
        Integer dreamDayCntByYy = jrnlSumryRepository.getTotalDreamDayCnt();
        totalSumry.setDreamDayCnt(dreamDayCntByYy);
        // 해당 년도 꿈 조회해서 갱신
        Integer dreamCntByYy = jrnlSumryRepository.getTotalDreamCnt();
        totalSumry.setDreamCnt(dreamCntByYy);
        // 해당 년도 일기 일자 조회해서 갱신
        Integer diaryCntByYy = jrnlSumryRepository.getTotalDiaryDayCnt();
        totalSumry.setDiaryDayCnt(diaryCntByYy);

        return totalSumry;
    }

    /**
     * 캐시 사용 위해 구현체로 pullUp
     */
    @Cacheable(value="jrnlSumryDtl", key="#key")
    public JrnlSumryDto.DTL getSumryDtl(final Integer key) throws Exception {
        return this.getDtlDto(key);
    }

    /**
     * 캐시 사용 위해 구현체로 pullUp
     */
    @Cacheable(value="jrnlSumryDtlByYy", key="#yy")
    public JrnlSumryDto getDtlDtoByYy(Integer yy) throws Exception {
        Optional<JrnlSumryEntity> entityWrapper = jrnlSumryRepository.findByYy(yy);
        if (entityWrapper.isEmpty()) return null;
        return jrnlSumryMapstruct.toDto(entityWrapper.get());
    }

    /**
     * 저널 결산 꿈 기록 완료 처리
     */
    public boolean dreamCompt(Integer key) throws Exception {
        JrnlSumryEntity entity = this.getDtlEntity(key);
        entity.setDreamComptYn("Y");
        jrnlSumryRepository.save(entity);

        // 캐시 초기화
        EhCacheUtils.evictCacheAll("jrnlSumryList");
        EhCacheUtils.evictCache("jrnlSumryDtl", entity.getPostNo());
        EhCacheUtils.evictCache("jrnlSumryDtlByYy", entity.getYy());
        return true;
    }
}