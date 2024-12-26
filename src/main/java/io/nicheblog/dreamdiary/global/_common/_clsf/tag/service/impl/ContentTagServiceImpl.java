package io.nicheblog.dreamdiary.global._common._clsf.tag.service.impl;

import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.global._common._clsf.tag.entity.ContentTagEntity;
import io.nicheblog.dreamdiary.global._common._clsf.tag.entity.TagEntity;
import io.nicheblog.dreamdiary.global._common._clsf.tag.mapstruct.ContentTagMapstruct;
import io.nicheblog.dreamdiary.global._common._clsf.tag.model.TagDto;
import io.nicheblog.dreamdiary.global._common._clsf.tag.repository.jpa.ContentTagRepository;
import io.nicheblog.dreamdiary.global._common._clsf.tag.service.ContentTagService;
import io.nicheblog.dreamdiary.global._common._clsf.tag.spec.ContentTagSpec;
import io.nicheblog.dreamdiary.global._common.cache.util.EhCacheUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
 */
@Service("contentTagService")
@RequiredArgsConstructor
@Log4j2
public class ContentTagServiceImpl
        implements ContentTagService {

    @Getter
    private final ContentTagRepository repository;
    @Getter
    private final ContentTagSpec spec;
    @Getter
    private final ContentTagMapstruct mapstruct = ContentTagMapstruct.INSTANCE;

    /**
     * 특정 게시물에 대한 콘텐츠 태그 목록을 조회합니다.
     *
     * @param clsfKey 참조 복합키 정보 (BaseClsfKey)
     * @return {@link List} -- 태그 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getTagStrListByClsfKey(final BaseClsfKey clsfKey) {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("refPostNo", clsfKey.getPostNo());
            put("refContentType", clsfKey.getContentType());
        }};
        List<ContentTagEntity> entityList = repository.findAll(spec.searchWith(searchParamMap));
        if (CollectionUtils.isEmpty(entityList)) return new ArrayList<>();
        return entityList.stream()
                .map(tag -> new TagDto(tag.getRefTagNo(), tag.getTagNm(), tag.getCtgr()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시물에 대해 태그 정보와 연결되지 않는 컨텐츠 태그 삭제.
     *
     * @param clsfKey 참조 복합키 정보 (BaseClsfKey)
     * @param obsoleteTagList 삭제할 태그 목록
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    @Transactional
    public void delObsoleteContentTags(final BaseClsfKey clsfKey, final List<TagDto> obsoleteTagList) throws Exception {
        String contentType = clsfKey.getContentType();
        obsoleteTagList.forEach(tag -> {
            repository.deleteObsoleteContentTags(clsfKey.getPostNo(), contentType, tag.getTagNm(), tag.getCtgr());
            // 태그 캐시 처리
            String cacheName = this.getCacheNameByContentType(contentType);
            this.evictCacheForPeriod(cacheName, tag.getTagNo());
        });
    }

    /**
     * 특정 게시물에 대해 컨텐츠 태그 목록 추가.
     *
     * @param clsfKey 참조 복합키 정보 (BaseClsfKey)
     * @param rsList 처리할 태그 엔티티 목록 (List<TagEntity>)
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    @Transactional
    public void addContentTags(final BaseClsfKey clsfKey, final List<TagEntity> rsList) throws Exception {
        List<ContentTagEntity> contentTagList = rsList.stream()
                .map(tag -> new ContentTagEntity(tag.getTagNo(), clsfKey))
                .collect(Collectors.toList());
        this.registAll(contentTagList);
    }

    /**
     * 컨텐츠 태그 bulk 등록 후처리. (override)
     *
     * @param entityList 등록된 콘텐츠 태그 엔티티 목록
     */
    @Override
    public void postRegistAll(final List<ContentTagEntity> entityList) {
        // 태그 개수 캐시 초기화
        entityList.forEach(entity -> {
            String contentType = entity.getRefContentType();
            String cacheName = this.getCacheNameByContentType(contentType);
            if (cacheName.isEmpty()) return;
            Integer tagNo = entity.getRefTagNo();;
            this.evictCacheForPeriod(cacheName, tagNo);
        });
    }

    /**
     * 특정 게시물에 대해 기존 콘텐츠 태그를 모두 삭제합니다.
     *
     * @param clsfKey 참조 복합키 정보 (BaseClsfKey)
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    @Transactional
    public void delExistingContentTags(final BaseClsfKey clsfKey) throws Exception {
        // 2. 글번호 + 태그번호를 받아와서 기존 태그 목록 조회
        Map<String, Object> searchParamMap = new HashMap<>() {{
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
     *
     * @param contentType 콘텐츠 유형 (String)
     * @return {@link String} -- 해당 콘텐츠 유형에 맞는 캐시 이름.
     */
    @Override
    public String getCacheNameByContentType(final String contentType) {
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
     *
     * @param cacheName 캐시 이름
     * @param tagNo 태그 번호 (key)
     */
    @Override
    public void evictCacheForPeriod(final String cacheName, final Integer tagNo) {
        try {
            int currYy = DateUtils.getCurrYy();
            for (int yy = 2010; yy <= currYy; yy++) {
                for (int mnth = 1; mnth <= 12; mnth++) {
                    String cacheKey = tagNo + "_" + yy + "_" + mnth;
                    EhCacheUtils.evictCache(cacheName, cacheKey);
                }
            }
            EhCacheUtils.evictCache(cacheName, tagNo + "_9999_99");
        } catch (Exception e) {
            throw new RuntimeException("Failed to evict cache for tagNo: " + tagNo, e);
        }
    }
}