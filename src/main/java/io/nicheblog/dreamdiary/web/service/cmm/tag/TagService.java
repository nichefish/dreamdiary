package io.nicheblog.dreamdiary.web.service.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.TagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.TagRepository;
import io.nicheblog.dreamdiary.web.spec.cmm.tag.TagSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * TagService
 * <pre>
 *  게시판 태그 서비스 모듈
 *  ※게시판 태그(board_tag) = 게시판별 태그 정보. 게시판 게시물 태그(board_post_tag)와 1:N으로 연관된다.
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
    //     // 태그를 먼저 처리해준다.
    //     List<String> tagStrList = postDto.getTagStrList();
    //     if (CollectionUtils.isEmpty(tagList)) return;
    //     this.regPostList(tagList);
    //     List<ContentTagDto> postTagList = new ArrayList<>();
    //     for (TagDto tag : tagList) {
    //         postTagList.add(new ContentTagDto(tag.getTag()));
    //     }
    //     postDto.setTagList(postTagList);
    // }

    /**
     * 게시판 > 게시판 태그 목록 등록
     */
    // public Boolean regPostList(final List<TagDto> tagDtoList) throws Exception {
    //     List<TagEntity> tagEntityList = new ArrayList<>();
    //     for (TagDto tag : tagDtoList) {
    //         // Dto -> Entity
    //         TagEntity tagEntity = tagMapstruct.toEntity(tag);
    //         tagEntityList.add(tagEntity);
    //     }
    //     // insert
    //     if (!CollectionUtils.isEmpty(tagEntityList)) tagRepository.saveAll(tagEntityList);
    //     return true;
    // }

}
