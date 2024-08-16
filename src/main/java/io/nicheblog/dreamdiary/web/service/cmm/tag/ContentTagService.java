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
import io.nicheblog.dreamdiary.web.repository.cmm.tag.ContentTagRepository;
import io.nicheblog.dreamdiary.web.service.jrnl.day.JrnlDayService;
import io.nicheblog.dreamdiary.web.service.jrnl.diary.JrnlDiaryService;
import io.nicheblog.dreamdiary.web.service.jrnl.dream.JrnlDreamService;
import io.nicheblog.dreamdiary.web.spec.cmm.tag.ContentTagSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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
@Log4j2
public class ContentTagService
        implements BaseCrudService<ContentTagDto, ContentTagDto, Integer, ContentTagEntity, ContentTagRepository, ContentTagSpec, ContentTagMapstruct> {

    private final ContentTagMapstruct tagMapstruct = ContentTagMapstruct.INSTANCE;

    @Resource(name = "contentTagRepository")
    private ContentTagRepository contentTagRepository;
    @Resource(name = "contentTagSpec")
    private ContentTagSpec contentTagSpec;
    @Resource(name = "jrnlDayService")
    private JrnlDayService jrnlDayService;
    @Resource(name = "jrnlDiaryService")
    private JrnlDiaryService jrnlDiaryService;
    @Resource(name = "jrnlDreamService")
    private JrnlDreamService jrnlDreamService;

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
        // 관련 캐시 삭제
        this.evictClsfCache(clsfKey);
    }

    /**
     * 컨텐츠 태그 처리
     */
    public void procContentTags(BaseClsfKey clsfKey, List<TagEntity> rsList) throws Exception {
        List<ContentTagEntity> contentTagList = rsList.stream()
                .map(tag -> new ContentTagEntity(tag.getTagNo(), clsfKey))
                .collect(Collectors.toList());
        this.registAll(contentTagList);

        // 관련 캐시 클리어
        this.evictClsfCache(clsfKey);
    }

    /**
     *
     */
    @Override
    public void postRegistAll(List<ContentTagEntity> entityList) {
        entityList.forEach(entity -> {
            Integer tagNo = entity.getRefTagNo();
            try {
                int currYy = DateUtils.getCurrYy();
                for (int yy = 2010; yy <= currYy; yy++) {
                    for (int mnth = 1; mnth < 13; mnth++ ) {
                        String cacheKey = tagNo + "_" + yy + "_" + mnth;
                        log.info("evict content_tag key: {}", cacheKey);
                        EhCacheUtils.evictCache("countDreamSize", cacheKey);
                    }
                }
                EhCacheUtils.evictCache("countDreamSize", tagNo + "_9999_99");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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

        String contentType = clsfKey.getContentType();
        entityList.forEach(entity -> {
            String cacheName = "";
            if (ContentType.JRNL_DAY.key.equals(contentType)) {
                cacheName = "countDaySize";
            } else if (ContentType.JRNL_DIARY.key.equals(contentType)) {
                cacheName = "countDiarySize";
            } else if (ContentType.JRNL_DREAM.key.equals(contentType)) {
                cacheName = "countDreamSize";
            }

            Integer tagNo = entity.getRefTagNo();
            try {
                int currYy = DateUtils.getCurrYy();
                for (int yy = 2010; yy <= currYy; yy++) {
                    for (int mnth = 1; mnth < 13; mnth++ ) {
                        EhCacheUtils.evictCache(cacheName, tagNo + "_" + yy + "_" + mnth);
                    }
                }
                EhCacheUtils.evictCache(cacheName, tagNo + "_9999_99");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // 관련 캐시 클리어
        this.evictClsfCache(clsfKey);
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
        if (jrnlDay == null) jrnlDay = jrnlDayService.getDtlDto(postNo);
        // 년도-월에 따른 캐시 삭제
        String yy = jrnlDay.getYy();
        String mnth = jrnlDay.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayList", yy + "_" + mnth);
        EhCacheUtils.evictCache("jrnlDayList", yy + "_99");
        EhCacheUtils.evictCache("jrnlDayDtlDto", postNo);
        // jrnl_day_tag
        EhCacheUtils.evictCache("jrnlDayTagList", yy + "_" + mnth);
        EhCacheUtils.evictCache("jrnlDayTagList", yy + "_99");
        EhCacheUtils.evictCache("jrnlDayTagList", "9999_99");
        EhCacheUtils.evictCache("jrnlDaySizedTagList", yy + "_" + mnth);
        EhCacheUtils.evictCache("jrnlDaySizedTagList", yy + "_99");
        EhCacheUtils.evictCache("jrnlDaySizedTagList",  "9999_99");
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
        if (jrnlDiary == null) jrnlDiary = jrnlDiaryService.getDtlDto(postNo);
        EhCacheUtils.evictCache("jrnlDiaryDtlDto", postNo);
        // 년도-월에 따른 캐시 삭제
        Integer yy = jrnlDiary.getYy();
        Integer mnth = jrnlDiary.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayList", yy + "_" + mnth);
        EhCacheUtils.evictCache("jrnlDayList", yy + "_99");
        EhCacheUtils.evictCache("jrnlDayDtlDto", jrnlDiary.getJrnlDayNo());
        // jrnl_diary_tag
        EhCacheUtils.evictCache("jrnlDiaryTagList", yy + "_" + mnth);
        EhCacheUtils.evictCache("jrnlDiaryTagList", yy + "_99");
        EhCacheUtils.evictCache("jrnlDiaryTagList", "9999_99");
        EhCacheUtils.evictCache("jrnlDiarySizedTagList", yy + "_" + mnth);
        EhCacheUtils.evictCache("jrnlDiarySizedTagList", yy + "_99");
        EhCacheUtils.evictCache("jrnlDiarySizedTagList",  "9999_99");
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlDiaryTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDiaryContentTagEntity.class);
    }

    /** 저널 꿈 관련 캐시 삭제 :: 메소드 분리 */
    public void evictJrnlDreamCache(Integer postNo) throws Exception {
        // jrnl_day
        EhCacheUtils.evictCacheAll("jrnlDayList");
        // 태그가 삭제되었을 때 태그 목록 캐시 초기화
        JrnlDreamDto jrnlDream = (JrnlDreamDto) EhCacheUtils.getObjectFromCache("jrnlDreamDtlDto", postNo);
        if (jrnlDream == null) jrnlDream = jrnlDreamService.getDtlDto(postNo);
        EhCacheUtils.evictCache("jrnlDreamDtlDto", postNo);
        // 년도-월에 따른 캐시 삭제
        Integer yy = jrnlDream.getYy();
        Integer mnth = jrnlDream.getMnth();
        // jrnl_day
        EhCacheUtils.evictCache("jrnlDayList", yy + "_" + mnth);
        EhCacheUtils.evictCache("jrnlDayList", yy + "_99");
        EhCacheUtils.evictCache("jrnlDayDtlDto", jrnlDream.getJrnlDayNo());
        // jrnl_dream_tag
        EhCacheUtils.evictCache("jrnlDreamTagList", yy + "_" + mnth);
        EhCacheUtils.evictCache("jrnlDreamTagList", yy + "_99");
        EhCacheUtils.evictCache("jrnlDreamTagList", "9999_99");
        EhCacheUtils.evictCache("jrnlDreamSizedTagList", yy + "_" + mnth);
        EhCacheUtils.evictCache("jrnlDreamSizedTagList", yy + "_99");
        EhCacheUtils.evictCache("jrnlDreamSizedTagList",  "9999_99");
        // L2캐시 처리
        EhCacheUtils.clearL2Cache(JrnlDreamTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDreamContentTagEntity.class);
    }
}
