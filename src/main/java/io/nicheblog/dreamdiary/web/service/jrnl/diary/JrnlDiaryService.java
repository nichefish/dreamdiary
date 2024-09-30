package io.nicheblog.dreamdiary.web.service.jrnl.diary;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseClsfService;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.web.event.EhCacheEvictEvent;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.diary.JrnlDiaryMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.diary.JrnlDiaryDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.diary.jpa.JrnlDiaryRepository;
import io.nicheblog.dreamdiary.web.repository.jrnl.diary.mybatis.JrnlDiaryMapper;
import io.nicheblog.dreamdiary.web.spec.jrnl.diary.JrnlDiarySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JrnlDiaryService
 * <pre>
 *  저널 일기 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseClsfService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlDiaryService")
@RequiredArgsConstructor
@Log4j2
public class JrnlDiaryService
        implements BaseClsfService<JrnlDiaryDto, JrnlDiaryDto, Integer, JrnlDiaryEntity, JrnlDiaryRepository, JrnlDiarySpec, JrnlDiaryMapstruct> {

    private final JrnlDiaryRepository jrnlDiaryRepository;
    private final JrnlDiarySpec jrnlDiarySpec;
    private final JrnlDiaryMapstruct jrnlDiaryMapstruct = JrnlDiaryMapstruct.INSTANCE;

    private final JrnlDiaryMapper jrnlDiaryMapper;
    private final ApplicationEventPublisher publisher;

    private final String JRNL_DIARY = ContentType.JRNL_DIARY.key;

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
     * 특정 태그의 관련 꿈 목록 조회
     */
    @Cacheable(value="jrnlDiaryTagDtl", key="#searchParam.hashCode()")
    public List<JrnlDiaryDto> jrnlDiaryTagDtl(final BaseSearchParam searchParam) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getListDto(searchParamMap);
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
        publisher.publishEvent(new EhCacheEvictEvent(this, rslt.getPostNo(), JRNL_DIARY));
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
        publisher.publishEvent(new EhCacheEvictEvent(this, rslt.getPostNo(), JRNL_DIARY));
    }

    /**
     * 삭제 후처리 :: override
     */
    @Override
    public void postDelete(final JrnlDiaryEntity rslt) throws Exception {
        // 관련 캐시 처리
        publisher.publishEvent(new EhCacheEvictEvent(this, rslt.getPostNo(), JRNL_DIARY));
        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 삭제 데이터 조회
     */
    public JrnlDiaryDto getDeletedDtlDto(Integer postNo) throws Exception {
        return jrnlDiaryMapper.getDeletedByPostNo(postNo);
    }
}