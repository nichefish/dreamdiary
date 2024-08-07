package io.nicheblog.dreamdiary.web.service.jrnl.diary;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseClsfService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.diary.JrnlDiaryMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.diary.JrnlDiaryDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.diary.JrnlDiaryRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.diary.JrnlDiarySpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JrnlDiaryService
 * <pre>
 *  저널 꿈 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseClsfService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlDiaryService")
@Log4j2
public class JrnlDiaryService
        implements BaseClsfService<JrnlDiaryDto, JrnlDiaryDto, Integer, JrnlDiaryEntity, JrnlDiaryRepository, JrnlDiarySpec, JrnlDiaryMapstruct> {

    private final JrnlDiaryMapstruct jrnlDiaryMapstruct = JrnlDiaryMapstruct.INSTANCE;

    @Resource(name = "jrnlDiaryRepository")
    private JrnlDiaryRepository jrnlDiaryRepository;
    @Resource(name = "jrnlDiarySpec")
    private JrnlDiarySpec jrnlDiarySpec;

    @Override
    public JrnlDiaryRepository getRepository() {
        return this.jrnlDiaryRepository;
    }
    @Override
    public JrnlDiaryMapstruct getMapstruct() {
        return this.jrnlDiaryMapstruct;
    }
    @Override
    public JrnlDiarySpec getSpec() {
        return this.jrnlDiarySpec;
    }

    /**
     * 특정 년도의 중요 일기 목록 조회
     */
    @Cacheable(value="imprtcDiaryList", key="#yy")
    public List<JrnlDiaryDto> getImprtcDiaryList(Integer yy) throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("yy", yy);
            put("imprtcYn", "Y");
        }};
        List<JrnlDiaryDto> imprtcDiaryList = this.getListDto(searchParamMap);
        Collections.sort(imprtcDiaryList);
        return imprtcDiaryList;
    }

    /**
     * 등록 전처리 :: override
     */
    @Override
    public void preRegist(final JrnlDiaryDto jrnlDiary) {
        // 인덱스(정렬순서) 처리
        Integer lastIndex = jrnlDiaryRepository.findLastIndexByJrnlDay(jrnlDiary.getJrnlDayNo()).orElse(0);
        jrnlDiary.setIdx(lastIndex + 1);
    }

    /**
     * 등록 후처리 :: override
     */
    @Override
    public void postRegist(final JrnlDiaryEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 상세 조회 (dto level) :: 캐시 처리
     */
    @Cacheable(value="jrnlDiaryDtlDto", key="#key")
    public JrnlDiaryDto getDtlDtoWithCache(Integer key) throws Exception {
        return this.getDtlDto(key);
    }

    /**
     * 수정 후처리 :: override
     */
    @Override
    public void postModify(final JrnlDiaryEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);
    }

    /**
     * 삭제 후처리 :: override
     */
    @Override
    public void postDelete(final JrnlDiaryEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictRelatedCache(rslt);

        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 관련 캐시 처리 :: 메소드 분리
     */
    public void evictRelatedCache(final JrnlDiaryEntity rslt) {
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayList", rslt.getJrnlDay().getYy() + "_" + rslt.getJrnlDay().getMnth());
        EhCacheUtils.evictCache("jrnlDayList", rslt.getJrnlDay().getYy() + "_99");
        EhCacheUtils.evictCache("jrnlDayDtlDto", rslt.getJrnlDayNo());
        // jrnl_diary
        EhCacheUtils.evictCache("imprtcDiaryList", rslt.getJrnlDay().getYy());
        EhCacheUtils.evictCache("jrnlDiaryDtlDto", rslt.getPostNo());
    }
}