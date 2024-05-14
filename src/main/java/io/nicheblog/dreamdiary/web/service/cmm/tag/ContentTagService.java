package io.nicheblog.dreamdiary.web.service.cmm.tag;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.ContentTagRepository;
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
    public void delObsoleteContentTags(BaseClsfKey clsfKey, List<TagDto> obsoleteTagList) {


        String contentType = clsfKey.getContentType();
        obsoleteTagList.forEach(tag -> {
            if (ContentType.JRNL_DREAM.key.equals(contentType)) {
                // 태그가 삭제되었을 때 태그 목록 캐시 초기화
                Integer postNo = clsfKey.getPostNo();
                JrnlDreamDto jrnlDream = (JrnlDreamDto) EhCacheUtils.getObjectFromCache("jrnlDreamDtlDto", postNo);
                if (jrnlDream == null) {
                    try {
                        jrnlDream = jrnlDreamService.getDtlDto(postNo);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                Integer yy = jrnlDream.getYy();
                Integer mnth = jrnlDream.getMnth();
                EhCacheUtils.evictCache("jrnlDreamTagList", yy + "_" + mnth);
                EhCacheUtils.evictCache("jrnlDreamTagList", yy + "_99");
                EhCacheUtils.evictCache("jrnlDreamTagList", "9999_99");
            }
        });
        obsoleteTagList.forEach(tag -> {
            contentTagRepository.deleteObsoleteContentTags(clsfKey.getPostNo(), contentType, tag.getTagNm(), tag.getCtgr());
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
        this.deleteAll(searchParamMap);

        // 관련 캐시 클리어
        this.evictClsfCache(clsfKey);
    }

    /**
     *
     */
    @Override
    public void postDeleteAll(List<ContentTagEntity> entityList) {
        entityList.forEach(entity -> {
            Integer tagNo = entity.getRefTagNo();
            try {
                int currYy = DateUtils.getCurrYy();
                for (int yy = 2010; yy <= currYy; yy++) {
                    for (int mnth = 1; mnth < 13; mnth++ ) {
                        EhCacheUtils.evictCache("countDreamSize", tagNo + "_" + yy + "_" + mnth);
                    }
                }
                EhCacheUtils.evictCache("countDreamSize", tagNo + "_9999_99");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * clsfKey로 관련 캐시 처리
     */
    public void evictClsfCache(BaseClsfKey clsfKey) throws Exception {
        String contentType = clsfKey.getContentType();
        Integer postNo = clsfKey.getPostNo();
        if (ContentType.JRNL_DIARY.key.equals(contentType)) {
            // jrnl_day
            EhCacheUtils.evictCacheAll("jrnlDayList");
            // jrnl_dream
            EhCacheUtils.evictCache("jrnlDiaryDtlDto", postNo);
        }
        if (ContentType.JRNL_DREAM.key.equals(contentType)) {
            // jrnl_dream
            JrnlDreamDto jrnlDream = (JrnlDreamDto) EhCacheUtils.getObjectFromCache("jrnlDreamDtlDto", postNo);
            if (jrnlDream == null) jrnlDream = jrnlDreamService.getDtlDto(postNo);
            Integer yy = jrnlDream.getYy();
            Integer mnth = jrnlDream.getMnth();
            EhCacheUtils.evictCache("jrnlDreamDtlDto", postNo);
            // jrnl_dream_tag
            EhCacheUtils.evictCacheAll("jrnlDreamTagDtl");
            EhCacheUtils.evictCache("jrnlDreamSizedTagList", yy + "_" + mnth);
            EhCacheUtils.evictCache("jrnlDreamSizedTagList", yy + "_99");
            EhCacheUtils.evictCache("jrnlDreamSizedTagList",  "9999_99");
            // jrnl_day
            EhCacheUtils.evictCache("jrnlDayList", yy + "_" + mnth);
            EhCacheUtils.evictCache("jrnlDayList", yy + "_99");
            EhCacheUtils.evictCache("jrnlDayList", yy + "_99");
        }
        if (ContentType.JRNL_SUMRY_CN.key.equals(contentType)) {
            // jrnl_sumry_cn
            EhCacheUtils.evictCache("jrnlSumryCnDtlDto", postNo);
        }

        EhCacheUtils.clearL2Cache(JrnlDreamTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDreamContentTagEntity.class);
    }
}
