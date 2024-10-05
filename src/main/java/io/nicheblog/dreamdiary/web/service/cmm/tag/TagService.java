package io.nicheblog.dreamdiary.web.service.cmm.tag;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.QTagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.TagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagSearchParam;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.jpa.TagRepository;
import io.nicheblog.dreamdiary.web.service.jrnl.dream.JrnlDreamService;
import io.nicheblog.dreamdiary.web.spec.cmm.tag.TagSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
@RequiredArgsConstructor
@Log4j2
public class TagService
        implements BaseCrudService<TagDto, TagDto, Integer, TagEntity, TagRepository, TagSpec, TagMapstruct> {

    private final TagRepository tagRepository;
    private final TagSpec tagSpec;
    private final TagMapstruct tagMapstruct = TagMapstruct.INSTANCE;

    private final ContentTagService contentTagService;
    private final JrnlDreamService jrnlDreamService;
    private final JPAQueryFactory queryFactory;

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
     * 태그 요소를 관리할 컨텐츠 타입 목록 조회
     */
    public List<ContentType> getContentTypeList() {
        return List.of(
                ContentType.JRNL_DAY,
                ContentType.JRNL_DIARY,
                ContentType.JRNL_DREAM
        );
    }

    /**
     * 컨텐츠 타입에 해당하는 태그만 INNER-JOIN으로 조회
     */
    public List<TagDto> getContentSpecificTagList(final ContentType contentType) {
        return this.getContentSpecificTagList(contentType.key);
    }
    public List<TagDto> getContentSpecificTagList(final String contentType) {
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
     * 컨텐츠 타입에 해당하는 태그만 INNER-JOIN으로 조회 (사이즈 정보 포함)
     */
    public List<TagDto> getContentSpecificSizedTagList(final ContentType contentType) {
        List<TagDto> tagList = this.getContentSpecificTagList(contentType);

        int maxSize = this.calcMaxSize(tagList, contentType);
        final int MIN_SIZE = 2; // 최소 크기
        final int MAX_SIZE = 9; // 최대 크기
        return tagList.stream()
                .peek(dto -> {
                    int size = dto.getContentSize();
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
     * 컨텐츠 타입 무관하게 사이즈 정보 포함하여 조회
     */
    public List<TagDto> getOverallSizedTagList(TagSearchParam searchParam) throws Exception {
        List<TagDto> tagList = this.getListDto(searchParam);
        String refContentType = searchParam.getContentType();

        int maxSize = this.calcMaxSize(tagList, refContentType);
        final int MIN_SIZE = 2; // 최소 크기
        final int MAX_SIZE = 9; // 최대 크기
        return tagList.stream()
                .peek(dto -> {
                    int size = dto.getContentSize();
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
    public Integer calcMaxSize(final List<TagDto> tagList, final ContentType contentType) {
        return this.calcMaxSize(tagList, contentType.key);
    }
    public Integer calcMaxSize(final List<TagDto> tagList, final String contentType) {
        int maxFrequency = 0;
        for (TagDto tag : tagList) {
            // 캐싱 처리 위해 셀프 프록시
            Integer tagSize = this.countTagSize(tag.getTagNo(), contentType);
            tag.setContentSize(tagSize);
            maxFrequency = Math.max(maxFrequency, tagSize);
        }
        return maxFrequency;
    }

    public Integer countTagSize(final Integer tagNo, final String contentType) {
        return tagRepository.countTagSize(tagNo, contentType);
    }

    /**
     * 컨텐츠 태그 처리
     */
    @Transactional
    public void procTags(final BaseClsfKey clsfKey, final TagCmpstn tagCmpstn) throws Exception {
        // 태그객체가 넘어오지 않았으면? 리턴.
        if (tagCmpstn == null) return;

        // 기존 태그와 컨텐츠 태그가 동일하면 리턴
        List<TagDto> existingTagList = contentTagService.getTagStrListByClsfKey(clsfKey);
        List<TagDto> newTagList = tagCmpstn.getParsedTagList();
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
    }

    /**
     * 마스터 태그 처리:: 메소드 분리
     */
    public List<TagEntity> addMasterTag(final List<TagDto> tagList, final BaseClsfKey clsfKey) {

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
     * 전체 태그 카테고리 목록 조회
     */
    public List<String> getTotalCtgrList() throws Exception {
        // TODO: 리포지토리 레이어로 분리하기
        QTagEntity tag = QTagEntity.tagEntity;
        return queryFactory.select(tag.ctgr)
                .distinct()
                .from(tag)
                .where(tag.ctgr.isNotEmpty())
                .fetch();
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
