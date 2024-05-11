package io.nicheblog.dreamdiary.web.service.cmm.tag;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.ContentTagRepository;
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
    public List<String> getTagStrListByClsfKey(BaseClsfKey clsfKey) {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("refPostNo", clsfKey.getPostNo());
            put("refContentType", clsfKey.getContentType());
        }};
        List<ContentTagEntity> entityList = contentTagRepository.findAll(contentTagSpec.searchWith(searchParamMap));
        if (CollectionUtils.isEmpty(entityList)) return new ArrayList<>();
        return entityList.stream()
                .map(ContentTagEntity::getTagNm)
                .collect(Collectors.toList());
    }

    /**
     * obsolete된 기존 컨텐츠 태그 삭제:: 메소드 분리
     */
    public void delObsoleteContentTags(BaseClsfKey clsfKey, List<String> obsoleteTagList) {
        contentTagRepository.deleteObsoleteContentTags(clsfKey.getPostNo(), clsfKey.getContentType(), obsoleteTagList);
    }

    /**
     * 컨텐츠 태그 처리
     */
    public void procContentTags(BaseClsfKey clsfKey, List<TagEntity> rsList) {
        List<ContentTagEntity> contentTagList = rsList.stream()
                .map(tag -> {
                    return new ContentTagEntity(tag.getTagNo(), clsfKey);
                })
                .collect(Collectors.toList());
        this.registAll(contentTagList);

        // 관련 캐시 클리어
        this.evictClsfCache(clsfKey);
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
     * clsfKey로 관련 캐시 처리
     */
    public void evictClsfCache(BaseClsfKey clsfKey) {
        String contentType = clsfKey.getContentType();
        Integer postNo = clsfKey.getPostNo();
        if (ContentType.JRNL_DIARY.key.equals(contentType)) {
            // jrnl_day
            EhCacheUtils.evictCacheAll("jrnlDayList");
            // jrnl_dream
            EhCacheUtils.evictCache("jrnlDiaryDtlDto", postNo);
        }
        if (ContentType.JRNL_DREAM.key.equals(contentType)) {
            // jrnl_day
            EhCacheUtils.evictCacheAll("jrnlDayList");
            // jrnl_dream
            EhCacheUtils.evictCache("jrnlDreamDtlDto", postNo);
            EhCacheUtils.evictCacheAll("jrnlDreamSizedTagList");
            EhCacheUtils.evictCacheAll("jrnlDreamTagDtl");
        }
        if (ContentType.JRNL_SUMRY_CN.key.equals(contentType)) {
            // jrnl_sumry_cn
            EhCacheUtils.evictCache("jrnlSumryCnDtlDto", postNo);
        }
    }
}
