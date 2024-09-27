package io.nicheblog.dreamdiary.web.service.cmm.tag;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDto;
import io.nicheblog.dreamdiary.web.model.jrnl.diary.JrnlDiaryDto;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.jpa.ContentTagRepository;
import io.nicheblog.dreamdiary.web.service.jrnl.day.JrnlDayService;
import io.nicheblog.dreamdiary.web.service.jrnl.diary.JrnlDiaryService;
import io.nicheblog.dreamdiary.web.service.jrnl.dream.JrnlDreamService;
import io.nicheblog.dreamdiary.web.spec.cmm.tag.ContentTagSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ContentTagService
 * <pre>
 *  컨텐츠-태그 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("contentTagService")
@RequiredArgsConstructor
@Log4j2
public class ContentTagService
        implements BaseCrudService<ContentTagDto, ContentTagDto, Integer, ContentTagEntity, ContentTagRepository, ContentTagSpec, ContentTagMapstruct> {

    private final ContentTagRepository contentTagRepository;
    private final ContentTagSpec contentTagSpec;
    private final ContentTagMapstruct tagMapstruct = ContentTagMapstruct.INSTANCE;

    private final JrnlDayService jrnlDayService;
    private final JrnlDiaryService jrnlDiaryService;
    private final JrnlDreamService jrnlDreamService;

    @Override
    public ContentTagRepository getRepository() {
        return this.contentTagRepository;
    }
    @Override
    public ContentTagSpec getSpec() {
        return this.contentTagSpec;
    }
    @Override
    public ContentTagMapstruct getMapstruct() {
        return this.tagMapstruct;
    }

    /**
     * 특정 게시물에 대한 컨텐츠 태그 목록 조회 
     */
    public List<TagDto> getTagStrListByClsfKey(BaseClsfKey clsfKey) {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("refPostNo", clsfKey.getPostNo());
            put("refContentType", clsfKey.getContentType());
        }};
        List<ContentTagEntity> entityList = contentTagRepository.findAll(contentTagSpec.searchWith(searchParamMap));
        if (CollectionUtils.isEmpty(entityList)) return new ArrayList<>();
        return entityList.stream()
                .map(tag -> new TagDto(tag.getTagNm(), tag.getCtgr()))
                .collect(Collectors.toList());
    }

    /**
     * obsolete된 기존 컨텐츠 태그 삭제:: 메소드 분리
     */
    public void delObsoleteContentTags(BaseClsfKey clsfKey, List<TagDto> obsoleteTagList) throws Exception {
        obsoleteTagList.forEach(tag -> {
            contentTagRepository.deleteObsoleteContentTags(clsfKey.getPostNo(), clsfKey.getContentType(), tag.getTagNm(), tag.getCtgr());
        });
    }

    /**
     * 컨텐츠 태그 처리
     */
    public void procContentTags(BaseClsfKey clsfKey, List<TagEntity> rsList) throws Exception {
        List<ContentTagEntity> contentTagList = rsList.stream()
                .map(tag -> new ContentTagEntity(tag.getTagNo(), clsfKey))
                .collect(Collectors.toList());
        this.registAll(contentTagList);
    }

    /**
     * 컨텐츠 태그 전처리::
     */
    @Override
    public void postRegistAll(List<ContentTagEntity> entityList) {
        // 태그 개수 캐시 초기화
        entityList.forEach(entity -> {
            String contentType = entity.getRefContentType();
            String cacheName = this.getCacheNameByContentType(contentType);
            if ("".equals(cacheName)) return;
            Integer tagNo = entity.getRefTagNo();;
            this.evictCacheForPeriod(cacheName, tagNo);
        });
    }
    
    /**
     * 기존 컨텐츠 태그 전부 삭제:: 메소드 분리
     */
    public void delExistingContentTags(BaseClsfKey clsfKey) throws Exception {
        // 2. 글번호 + 태그번호를 받아와서 기존 태그 목록 조회
        Map<String, Object> searchParamMap = new HashMap(){{
            put("refPostNo", clsfKey.getPostNo());
            put("refContentType", clsfKey.getContentType());
        }};
        List<ContentTagEntity> entityList = this.getListEntity(searchParamMap);
        this.deleteAll(entityList);

        // 태그 개수 캐시 초기화
        String contentType = clsfKey.getContentType();
        String cacheName = this.getCacheNameByContentType(contentType);
        entityList.forEach(entity -> {
            Integer tagNo = entity.getRefTagNo();
            this.evictCacheForPeriod(cacheName, tagNo);
        });
    }

    /**
     * 콘텐츠 타입에 따른 캐시 이름 반환 :: 메소드 분리
     */
    private String getCacheNameByContentType(String contentType) {
        if (ContentType.JRNL_DAY.key.equals(contentType)) {
            return "countDaySize";
        } else if (ContentType.JRNL_DIARY.key.equals(contentType)) {
            return "countDiarySize";
        } else if (ContentType.JRNL_DREAM.key.equals(contentType)) {
            return "countDreamSize";
        }
        return "";
    }

    /**
     * 기간에 따른 컨텐츠 태그 캐시 삭제 :: 메소드 분리
     */
    private void evictCacheForPeriod(String cacheName, Integer tagNo) {
        try {
            int currYy = DateUtils.getCurrYy();
            for (int yy = 2010; yy <= currYy; yy++) {
                for (int mnth = 1; mnth <= 12; mnth++) {
                    EhCacheUtils.evictCache(cacheName, tagNo + "_" + yy + "_" + mnth);
                }
            }
            EhCacheUtils.evictCache(cacheName, tagNo + "_9999_99");
        } catch (Exception e) {
            throw new RuntimeException("Failed to evict cache for tagNo: " + tagNo, e);
        }
    }

    /**
     * clsfKey로 관련 캐시 처리
     */
    public void evictClsfCache(BaseClsfKey clsfKey) throws Exception {
        String contentType = clsfKey.getContentType();
        Integer postNo = clsfKey.getPostNo();
        if (ContentType.JRNL_DAY.key.equals(contentType)) {
            // 저널 일자 관련 캐시 삭제 :: 메소드 분리
            this.evictJrnlDayCache(postNo);
        } else if (ContentType.JRNL_DIARY.key.equals(contentType)) {
            // 저널 일기 관련 캐시 삭제 :: 메소드 분리
            this.evictJrnlDiaryCache(postNo);
        } else if (ContentType.JRNL_DREAM.key.equals(contentType)) {
            // 저널 꿈 관련 캐시 삭제 :: 메소드 분리
            this.evictJrnlDreamCache(postNo);
        } else if (ContentType.JRNL_SUMRY_CN.key.equals(contentType)) {
            // jrnl_sumry_cn
            EhCacheUtils.evictCache("jrnlSumryCnDtlDto", postNo);
        }
    }

    /** 저널 일자 관련 캐시 삭제 :: 메소드 분리 */
    public void evictJrnlDayCache(Integer postNo) throws Exception {
        // jrnl_day
        EhCacheUtils.evictCacheAll("jrnlDayList");
        // 태그가 삭제되었을 때 태그 목록 캐시 초기화
        JrnlDayDto jrnlDay = (JrnlDayDto) EhCacheUtils.getObjectFromCache("jrnlDayDtlDto", postNo);
        if (jrnlDay == null) {
            try {
                jrnlDay = jrnlDayService.getDtlDto(postNo);
            } catch (Exception e) {
                jrnlDay = jrnlDayService.getDeletedDtlDto(postNo);
                if (jrnlDay == null) return;
            }
        }
        // 년도-월에 따른 캐시 삭제
        String yy = jrnlDay.getYy();
        String mnth = jrnlDay.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayDtlDto", postNo);
        this.evictCacheForPeriod("jrnlDayList", yy, mnth);
        // jrnl_day_tag
        this.evictCacheForPeriod("jrnlDayTagList", yy, mnth);
        this.evictCacheForPeriod("jrnlDaySizedTagList", yy, mnth);
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlDayTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDayContentTagEntity.class);
    }

    /** 저널 일기 관련 캐시 삭제 :: 메소드 분리 */
    public void evictJrnlDiaryCache(Integer postNo) throws Exception {
        // jrnl_day
        EhCacheUtils.evictCacheAll("jrnlDayList");
        // 태그가 삭제되었을 때 태그 목록 캐시 초기화
        JrnlDiaryDto jrnlDiary = (JrnlDiaryDto) EhCacheUtils.getObjectFromCache("jrnlDiaryDtlDto", postNo);
        if (jrnlDiary == null) {
            try {
                jrnlDiary = jrnlDiaryService.getDtlDto(postNo);
            } catch (Exception e) {
                jrnlDiary = jrnlDiaryService.getDeletedDtlDto(postNo);
                if (jrnlDiary == null) return;
            }
        }
        // 상세 캐시 삭제
        EhCacheUtils.evictCache("jrnlDiaryDtlDto", postNo);
        // 년도-월에 따른 캐시 삭제
        Integer yy = jrnlDiary.getYy();
        Integer mnth = jrnlDiary.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayDtlDto", jrnlDiary.getJrnlDayNo());
        this.evictCacheForPeriod("jrnlDayList", yy, mnth);
        // jrnl_diary_tag
        this.evictCacheForPeriod("jrnlDiaryTagList", yy, mnth);
        this.evictCacheForPeriod("jrnlDiarySizedTagList", yy, mnth);
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlDiaryTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDiaryContentTagEntity.class);
    }

    /** 저널 꿈 관련 캐시 삭제 :: 메소드 분리 */
    public void evictJrnlDreamCache(Integer postNo) throws Exception {
        // jrnl_day
        EhCacheUtils.evictCacheAll("jrnlDayList");
        // 태그가 삭제되었을 때 태그 목록 캐시 초기화 (부재시 삭제 데이터 조회)
        JrnlDreamDto jrnlDream = (JrnlDreamDto) EhCacheUtils.getObjectFromCache("jrnlDreamDtlDto", postNo);
        if (jrnlDream == null) {
            try {
                jrnlDream = jrnlDreamService.getDtlDto(postNo);
            } catch (Exception e) {
                jrnlDream = jrnlDreamService.getDeletedDtlDto(postNo);
                if (jrnlDream == null) return;
            }
        }
        // 상세 캐시 삭제
        EhCacheUtils.evictCache("jrnlDreamDtlDto", postNo);
        // 년도-월에 따른 캐시 삭제
        Integer yy = jrnlDream.getYy();
        Integer mnth = jrnlDream.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayDtlDto", jrnlDream.getJrnlDayNo());
        this.evictCacheForPeriod("jrnlDayList", yy, mnth);
        // jrnl_dream_tag
        this.evictCacheForPeriod("jrnlDreamTagList", yy, mnth);
        this.evictCacheForPeriod("jrnlDreamSizedTagList", yy, mnth);
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlDreamTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDreamContentTagEntity.class);
    }

    /**
     * 캐시 이름에 대해서 기간 캐시 삭제 :: 메소드 분리
     */
    private void evictCacheForPeriod(String cacheName, String yy, String mnth) {
        this.evictCacheForPeriod(cacheName, Integer.parseInt(yy), Integer.parseInt(mnth));
    }
    private void evictCacheForPeriod(String cacheName, Integer yy, Integer mnth) {
        EhCacheUtils.evictCache(cacheName, yy + "_" + mnth);
        EhCacheUtils.evictCache(cacheName, yy + "_99");
        EhCacheUtils.evictCache(cacheName, "9999_99");
    }
}
