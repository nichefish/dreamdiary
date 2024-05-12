package io.nicheblog.dreamdiary.web.service.jrnl.dream;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseClsfService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.dream.JrnlDreamMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.dream.JrnlDreamRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.dream.JrnlDreamSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JrnlDreamService
 * <pre>
 *  저널 꿈 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseClsfService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlDreamService")
@Log4j2
public class JrnlDreamService
        implements BaseClsfService<JrnlDreamDto, JrnlDreamDto, Integer, JrnlDreamEntity, JrnlDreamRepository, JrnlDreamSpec, JrnlDreamMapstruct> {

    private final JrnlDreamMapstruct jrnlDreamMapstruct = JrnlDreamMapstruct.INSTANCE;

    @Resource(name = "jrnlDreamRepository")
    private JrnlDreamRepository jrnlDreamRepository;
    @Resource(name = "jrnlDreamSpec")
    private JrnlDreamSpec jrnlDreamSpec;

    @Override
    public JrnlDreamRepository getRepository() {
        return this.jrnlDreamRepository;
    }
    @Override
    public JrnlDreamMapstruct getMapstruct() {
        return this.jrnlDreamMapstruct;
    }
    @Override
    public JrnlDreamSpec getSpec() {
        return this.jrnlDreamSpec;
    }

    /**
     * 특정 년도의 중요 꿈 목록 조회
     */
    @Cacheable(value="imprtcDreamList", key="#yy")
    public List<JrnlDreamDto> getImprtcDreamList(Integer yy) throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("yy", yy);
            put("imprtcYn", "Y");
        }};
        List<JrnlDreamDto> imprtcDreamList = this.getListDto(searchParamMap);
        Collections.sort(imprtcDreamList);
        return imprtcDreamList;
    }

    /**
     * 특정 태그의 관련 꿈 목록 조회
     */
    @Cacheable(value="jrnlDreamTagDtl", key="#searchParam.hashCode()")
    public List<JrnlDreamDto> jrnlDreamTagDtl(final BaseSearchParam searchParam) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getListDto(searchParamMap);
    }

    /**
     * 등록 전처리 :: override
     */
    @Override
    public void preRegist(final JrnlDreamDto jrnlDream) {
        if (!"Y".equals(jrnlDream.getElseDreamYn())) {
            // 인덱스(정렬순서) 처리
            Integer lastIndex = jrnlDreamRepository.findLastIndexByJrnlDay(jrnlDream.getJrnlDayNo()).orElse(0);
            jrnlDream.setIdx(lastIndex + 1);
        }
    }

    /**
     * 등록 후처리 :: override
     */
    @Override
    public void postRegist(final JrnlDreamEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
        EhCacheUtils.evictCache("imprtcDreamList", rslt.getJrnlDay().getYy());
    }

    /**
     * 상세 조회 (dto level) :: 캐시 처리
     */
    @Cacheable(value="jrnlDreamDtlDto", key="#key")
    public JrnlDreamDto getDtlDtoWithCache(Integer key) throws Exception {
        return this.getDtlDto(key);
    }

    /**
     * 수정 후처리 :: override
     */
    @Override
    public void postModify(final JrnlDreamEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 삭제 후처리 :: override
     */
    @Override
    public void postDelete(final JrnlDreamEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);

        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 관련 캐시 처리 :: 메소드 분리
     */
    public void evictRelatedCache(final JrnlDreamEntity rslt) {
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayList", rslt.getJrnlDay().getYy() + "_" + rslt.getJrnlDay().getMnth());
        EhCacheUtils.evictCache("jrnlDayList", rslt.getJrnlDay().getYy() + "_99");
        EhCacheUtils.evictCache("jrnlDayDtlDto", rslt.getJrnlDayNo());
        // jrnl_dream
        EhCacheUtils.evictCache("imprtcDreamList", rslt.getJrnlDay().getYy());
        EhCacheUtils.evictCache("jrnlDreamDtlDto", rslt.getPostNo());
    }
}