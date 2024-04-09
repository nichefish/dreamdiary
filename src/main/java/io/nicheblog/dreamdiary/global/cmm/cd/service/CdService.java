package io.nicheblog.dreamdiary.global.cmm.cd.service;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.mapstruct.CdMapstruct;
import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCd;
import io.nicheblog.dreamdiary.global.cmm.cd.repository.CdRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * CmmCdService
 * <pre>
 *  공통 > 코드 관련 처리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service
public class CdService {

    private final CdMapstruct cdMapstruct = CdMapstruct.INSTANCE;

    @Resource(name = "cdRepository")
    private CdRepository cdRepository;

    /**
     * 공통 > 공통코드CL_CD로 상세코드DTL_CD 목록 조회 (entity level)
     * Cacheable (key:: #clCd)
     */
    @Cacheable(cacheNames = "cdEntityListByClCd", key = "#clCd", condition = "#clCd!=null")
    public List<DtlCdEntity> getCdEntityListByClCd(final String clCd) throws Exception {
        if (StringUtils.isEmpty(clCd)) return null;
        return cdRepository.findByClCdAndStateUseYn(clCd, "Y", Sort.by(Sort.Direction.ASC, "state.sortOrdr"));
    }

    /**
     * 공통 > 공통코드CL_CD로 상세코드DTL_CD 목록 조회 (dto level)
     * Cacheable (key:: #clCd)
     */
    @Cacheable(cacheNames = "cdDtoListByClCd", key = "#clCd", condition = "#clCd!=null")
    public List<DtlCd> getCdListByClCd(final String clCd) throws Exception {
        if (StringUtils.isEmpty(clCd)) return null;
        // 코드 목록 조회 (entity level)
        List<DtlCdEntity> rsDtlCdList = this.getCdEntityListByClCd(clCd);
        // Entity -> Dto
        List<DtlCd> rsDtlCdDtoList = new ArrayList<>();
        for (DtlCdEntity dtlCdEntity : rsDtlCdList) {
            rsDtlCdDtoList.add(cdMapstruct.toDto(dtlCdEntity));
        }
        return rsDtlCdDtoList;
    }

    /**
     * 공통 > 코드 정보 model에 추가 (가독성+편의상 분리)
     */
    public void setModelCdData(final String clCd, final ModelMap model) throws Exception {
        model.addAttribute(clCd, this.getCdListByClCd(clCd));
    }

    /**
     * 공통 > 공통코드CL_CD, 상세코드DTL_CD로 상세코드명 조회
     */
    public String getDtlCdNm(final String clCd, final String dtlCd) {
        if (StringUtils.isEmpty(clCd) || StringUtils.isEmpty(dtlCd)) return null;
        DtlCdEntity rsDtlCd = cdRepository.findByClCdAndDtlCd(clCd, dtlCd);
        if (rsDtlCd == null) return null;
        return rsDtlCd.getDtlCdNm();
    }
}