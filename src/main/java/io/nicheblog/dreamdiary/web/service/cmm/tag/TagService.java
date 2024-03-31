package io.nicheblog.dreamdiary.web.service.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.TagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.ContentTagRepository;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.TagRepository;
import io.nicheblog.dreamdiary.web.spec.cmm.tag.TagSpec;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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

    @Resource(name = "tagRepository")
    private TagRepository tagRepository;
    @Resource(name = "contentTagRepository")
    private ContentTagRepository contentTagRepository;
    @Resource(name = "tagSpec")
    private TagSpec tagSpec;

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

    public List<TagEntity> procTagList(TagEmbed tagEmbed) {
        List<String> tagStrList = tagEmbed.getTagStrList();
        if (CollectionUtils.isEmpty(tagStrList)) return new ArrayList<>();
        // 1, 마스터 태그 처리
        List<TagEntity> tagEntityList = tagStrList.stream()
                .distinct() // 중복된 태그 문자열 제거
                .map(tagStr -> tagRepository.findByTagNm(tagStr)
                        .orElseGet(() -> new TagEntity(tagStr))) // 데이터베이스에서 태그 조회, 없으면 새 객체 생성
                .collect(Collectors.toList());
        return tagRepository.saveAllAndFlush(tagEntityList);
    }

    public void procContentTagList(List<ContentTagEntity> contentTagList) {
        contentTagRepository.saveAllAndFlush(contentTagList);
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

    /**
     * 게시판 > 게시판 등록/수정 전처리 :: 메소드 분리
     */
    // public void processTagList(final BasePostDto postDto) throws Exception {

    // }
}
