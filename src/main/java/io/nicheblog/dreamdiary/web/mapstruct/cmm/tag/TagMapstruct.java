package io.nicheblog.dreamdiary.web.mapstruct.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * TagMapstruct
 * <pre>
 *  일반게시판 태그 MapStruct 기반 Mapper 인터페이스
 *  ※일반게시판 태그(board_tag) = 일반게시판별 태그 정보. 일반게시판 게시물 태그(board_post_tag)와 1:N으로 연관된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {})
public interface TagMapstruct
        extends BaseListMapstruct<TagDto, TagDto, TagEntity> {

    TagMapstruct INSTANCE = Mappers.getMapper(TagMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "contentTagList", expression = "java(entity.getContentTagDtoList())")
    TagDto toDto(final TagEntity entity) throws Exception;

    @Override
    @Mapping(target = "contentTagList", expression = "java(entity.getContentTagDtoList())")
    TagDto toListDto(final TagEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    TagEntity toEntity(final TagDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final TagDto dto,
            final @MappingTarget TagEntity entity
    ) throws Exception;
}
