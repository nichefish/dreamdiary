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

    // @Resource(name = "cmmCdMapper")
    // private CmmCdMapper cmmCdMapper;

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
     * 공통 > 공통코드CL_CD로 상세코드DTL_CD 관리용 (useYn="N" 포함) 목록 조회 (dto level)
     * Cacheable (key:: #clCd)
     */
    // public List<DtlCd> getCdMngListByClCd(final String clCd) throws Exception {
    //     // 코드 목록 조회 (entity level)
    //     List<DtlCd> rsDtlCdList = cmmCdMapper.getCdMngListByClCd(clCd);
    //     // Entity -> Dto
    //     long i = 1;
    //     for (DtlCd dtlCdDto : rsDtlCdList) {
    //         dtlCdDto.setRnum(i++);
    //     }
    //     return rsDtlCdList;
    // }

    /**
     * 공통 > 공통코드CL_CD로 상세코드DTL_CD *문자열* 목록 조회
     * Cacheable (key:: #clCd)
     */
    @Cacheable(cacheNames = "cdIdListByClCd", key = "#clCd", condition = "#clCd!=null")
    public List<String> getCdIdListByClCd(final String clCd) throws Exception {
        if (StringUtils.isEmpty(clCd)) return null;
        // 코드 목록 조회 (entity level)
        List<DtlCdEntity> rsDtlCdList = this.getCdEntityListByClCd(clCd);
        // Entity -> Dto
        List<String> rsDtlCdIdList = new ArrayList<>();
        for (DtlCdEntity dtlCdEntity : rsDtlCdList) {
            rsDtlCdIdList.add(dtlCdEntity.getDtlCd());
        }
        return rsDtlCdIdList;
    }

    /**
     * 공통 > 코드 정보 model에 추가 (가독성+편의상 분리)
     */
    public void setModelCdData(
            final String clCd,
            final ModelMap model
    ) throws Exception {
        model.addAttribute(clCd, this.getCdListByClCd(clCd));
    }

    /**
     * 공통 > 공통코드CL_CD, 상세코드DTL_CD로 상세코드명 조회
     */
    public String getDtlCdNm(
            final String clCd,
            final String dtlCd
    ) {
        if (StringUtils.isEmpty(clCd) || StringUtils.isEmpty(dtlCd)) return null;
        DtlCdEntity rsDtlCd = cdRepository.findByClCdAndDtlCd(clCd, dtlCd);
        if (rsDtlCd == null) return null;
        return rsDtlCd.getDtlCdNm();
    }

    /**
     * 공통 > 공통코드CL_CD, 상세코드DTL_CD로 상세코드 정보 조회
     */
    // public DtlCd getDtlCd(
    //         final String clCd,
    //         final String dtlCd
    // ) throws Exception {
    //     DtlCdEntity rsDtlCd = cdRepository.findByClCdAndDtlCd(clCd, dtlCd);
    //     return cmmCdMapstruct.toDto(rsDtlCd);
    // }

    /**
     * 공통 > 공통코드CL_CD, 상세코드DTL_CD로 상세코드 정보 조회
     */
    // public DtlCd getDtlCdMng(
    //         final String clCd,
    //         final String dtlCd
    // ) throws Exception {
    //     // TODO: JPA Repository로 바꾸기?
    //     return cmmCdMapper.getDtlCdMng(clCd, dtlCd);
    // }

    /**
     * 공통 > 상세코드 등록
     * (간이버전, 필수컬럼인 CL_CD와 DTL_CD만 사용)
     */
    // @CacheEvict(value = {"cdEntityListByClCd", "cdDtoListByClCd", "cdIdListByClCd"}, allEntries = true)
    // public Boolean regDtlCd(
    //         final String clCd,
    //         final String dtlCd
    // ) {
    //     DtlCdEntity rsDtlCd = cdRepository.findByClCdAndDtlCd(clCd, dtlCd);
    //     if (rsDtlCd != null) throw new DupRegException("이미 등록된 코드가 있습니다.");
//
    //     DtlCdEntity dlCd = new DtlCdEntity(clCd, dtlCd);
    //     cdRepository.save(dlCd);
    //     return true;
    // }

    /**
     * 공통 > 상세코드 등록
     */
    // @CacheEvict(value = {"cdEntityListByClCd", "cdDtoListByClCd", "cdIdListByClCd"}, allEntries = true)
    // public Boolean regDtlCd(final DtlCd dtlCdDto) throws Exception {
    //     DtlCdKey key = dtlCdDto.getKey();
    //     Optional<DtlCdEntity> rsWrapper = cdRepository.findById(key);
    //     if (rsWrapper.isPresent()) throw new DupRegException("이미 등록된 코드가 있습니다.");
//
    //     if (!"Y".equals(dtlCdDto.getUseYn())) dtlCdDto.setUseYn("N");
    //     DtlCdEntity dtlCdEntity = cmmCdMapstruct.toEntity(dtlCdDto);
    //     cdRepository.save(dtlCdEntity);
    //     return true;
    // }

    /**
     * 공통 > 상세코드 수정
     */
    // @CacheEvict(value = {"cdEntityListByClCd", "cdDtoListByClCd", "cdIdListByClCd"}, allEntries = true)
    // public boolean mdfDtlCd(final DtlCd dtlCdDto) throws Exception {
    //     DtlCdKey key = dtlCdDto.getKey();
    //     Optional<DtlCdEntity> rsWrapper = cdRepository.findById(key);
    //     if (rsWrapper.isEmpty()) throw new IllegalArgumentException("기존 데이터 조회에 실패했습니다.");
//
    //     if (!"Y".equals(dtlCdDto.getUseYn())) dtlCdDto.setUseYn("N");
    //     DtlCdEntity rsDtlCdEntity = rsWrapper.get();
    //     cmmCdMapstruct.updateFromDto(dtlCdDto, rsDtlCdEntity);
    //     cdRepository.save(rsDtlCdEntity);
    //     return true;
    // }

    /**
     * 공통 > 상세코드 삭제
     * 코드값 자체가 KEY값이기 때문에 DEL_YN만 처리하면 안되고 아예 삭제해야 한다.
     */
    // @CacheEvict(value = {"cdEntityListByClCd", "cdDtoListByClCd", "cdIdListByClCd"}, allEntries = true)
    // public Boolean delDtlCd(
    //         final String clCd,
    //         final String dtlCd
    // ) {
    //     DtlCdEntity rsDtlCdEntity = cdRepository.findByClCdAndDtlCd(clCd, dtlCd);
    //     if (rsDtlCdEntity == null) throw new IllegalArgumentException("기존 데이터 조회에 실패했습니다.");
//
    //     cdRepository.delete(rsDtlCdEntity);
    //     return true;
    // }
}