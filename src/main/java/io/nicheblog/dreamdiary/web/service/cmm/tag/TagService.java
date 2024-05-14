package io.nicheblog.dreamdiary.web.service.cmm.tag;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.TagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.TagRepository;
import io.nicheblog.dreamdiary.web.service.jrnl.dream.JrnlDreamService;
import io.nicheblog.dreamdiary.web.spec.cmm.tag.TagSpec;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TagService
 * <pre>
 *  태그 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("tagService")
@Log4j2
public class TagService
        implements BaseCrudService<TagDto, TagDto, Integer, TagEntity, TagRepository, TagSpec, TagMapstruct> {

    private final TagMapstruct tagMapstruct = TagMapstruct.INSTANCE;

    @Resource(name = "contentTagService")
    private ContentTagService contentTagService;
    @Resource(name = "tagRepository")
    private TagRepository tagRepository;
    @Resource(name = "tagSpec")
    private TagSpec tagSpec;
    @Resource(name = "jrnlDreamService")
    private JrnlDreamService jrnlDreamService;

    @Override
    public TagRepository getRepository() {
        return this.tagRepository;
    }
    @Override
    public TagSpec getSpec() {
        return this.tagSpec;
    }
    @Override
    public TagMapstruct getMapstruct() {
        return this.tagMapstruct;
    }

    /**
     * 컨텐츠 타입에 해당하는 태그만 INNER-JOIN으로 조회
     */
    public List<TagDto> getContentSpecificTagList(ContentType contentType) {
        return this.getContentSpecificTagList(contentType.key);
    }
    public List<TagDto> getContentSpecificTagList(String contentType) {
        List<TagEntity> contentSpeficitTagList = tagRepository.findAll(tagSpec.getContentSpecificTag(contentType));
        return contentSpeficitTagList.stream()
                .map(entity -> {
                    try {
                        return tagMapstruct.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 컨텐츠 태그 처리
     */
    @Transactional
    public void procTags(BaseClsfKey clsfKey, TagCmpstn tagCmpstn) throws Exception {

        // 태그객체 또는 태그 문자열이 넘어오지 않았으면? 리턴.
        if (tagCmpstn == null) return;
        List<TagDto> newTagList = tagCmpstn.getParsedTagList();
        if (CollectionUtils.isEmpty(newTagList)) return;

        // 기존 태그와 컨텐츠 태그가 동일하면 리턴
        List<TagDto> existingTagList = contentTagService.getTagStrListByClsfKey(clsfKey);
        boolean isSame = newTagList.size() == existingTagList.size() && new HashSet<>(newTagList).containsAll(existingTagList);
        if (isSame) return;
        
        // 1, 추가해야 할 마스터 태그 처리 (메소드 분리)
        // 새로운 태그 목록에서 기존 태그 목록을 빼면 추가해야 할 태그들이 나옴
        Set<TagDto> newTagSet = new HashSet<>(newTagList);
        existingTagList.forEach(newTagSet::remove);
        List<TagEntity> rsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(newTagSet)) {
            rsList = this.addMasterTag(new ArrayList<>(newTagSet), clsfKey);
        }

        // 2. 삭제해야 할 태그 삭제
        // 기존 태그 목록에서 새로운 태그 목록을 빼면 삭제해야 할 태그들이 나옴
        Set<TagDto> obsoleteTagSet = new HashSet<>(existingTagList);
        newTagList.forEach(obsoleteTagSet::remove);
        if (CollectionUtils.isNotEmpty(obsoleteTagSet)) contentTagService.delObsoleteContentTags(clsfKey, new ArrayList<>(obsoleteTagSet));

        // 3. 추가해야 할 컨텐츠-태그를 처리해준다.
        if (CollectionUtils.isNotEmpty(rsList)) contentTagService.procContentTags(clsfKey, rsList);

        // 4. 연관관계 없는 마스터 태그 삭제
        // this.deleteNoRefTags();

        // L2 캐시 클리어
        EhCacheUtils.clearL2Cache(JrnlDreamTagEntity.class);
        EhCacheUtils.clearL2Cache(JrnlDreamContentTagEntity.class);
    }

    /**
     * 마스터 태그 처리:: 메소드 분리
     */
    public List<TagEntity> addMasterTag(List<TagDto> tagList, BaseClsfKey clsfKey) {

        String contentType = clsfKey.getContentType();
        List<TagEntity> tagEntityList = tagList.stream()
                .distinct() // 중복된 태그 문자열 제거
                .map(tag -> tagRepository.findByTagNmAndCtgr(tag.getTagNm(), tag.getCtgr())
                        .orElseGet(() -> {
                            if (ContentType.JRNL_DREAM.key.equals(contentType)) {
                                // 새 태그가 추가되었을 때만 태그 목록 캐시 초기화
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
                            return new TagEntity(tag.getTagNm(), tag.getCtgr());
                        })) // 데이터베이스에서 태그 조회, 없으면 새 객체 생성
                .collect(Collectors.toList());

        return tagRepository.saveAllAndFlush(tagEntityList);
    }

    /**
     * 연관관계 없는 마스터 태그 삭제
     */
    public void deleteNoRefTags() {
        List<TagEntity> entity = tagRepository.findAll(tagSpec.getNoRefTags());
        tagRepository.deleteAll(entity);

    }

    /**
     * css 사이즈 계산한 태그 목록 조회
     * 태그 1개 = 1. 그 외엔 2~9
     */
    public List<TagDto> getSizedListDto(Map<String, Object> searchParamMap) throws Exception {
        List<TagDto> tagList = this.getListDto(searchParamMap);
        int maxSize = this.calcMaxSize(tagList);
        final int MIN_SIZE = 2; // 최소 크기
        final int MAX_SIZE = 9; // 최대 크기
        return tagList.stream()
                .peek(dto -> {
                    int size = dto.getSize();
                    if (size == 1) {
                        dto.setTagClass("ts-1");
                    } else {
                        double ratio = (double) size / maxSize; // 사용 빈도의 비율 계산
                        int tagSize = (int) (MIN_SIZE + (MAX_SIZE - MIN_SIZE) * ratio);
                        dto.setTagClass("ts-"+tagSize);
                    }
                })
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 최대 사용빈도 계산한 태그 목록 조회
     */
    public int calcMaxSize(List<TagDto> tagList) {
        int maxFrequency = 0;
        for (TagDto tag : tagList) {
            maxFrequency = Math.max(maxFrequency, tag.getSize());
        }
        return maxFrequency;
    }

    /**
     * 게시판 태그 목록 Page<Entity>->Page<Dto> 변환
     */
    // @Override
    //public Page<TagDto> pageEntityToDto(final Page<TagEntity> entityPage) throws Exception {
    //    List<TagDto> dtoList = new ArrayList<>();
    //    for (TagEntity entity : entityPage.getContent()) {
    //        if (CollectionUtils.isEmpty(entity.getPostTagList())) continue;
    //        dtoList.add(tagMapstruct.toDto(entity));
    //    }
    //    return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    //}
}
