package io.nicheblog.dreamdiary.web.mapstruct.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ContentTagMapstruct
 * <pre>
 *  일반게시판 게시물-태그 MapStruct 기반 Mapper 인터페이스
 *  일반게시판 게시물 태그(board_post_tag) = 일반게시판 태그(board_tag)에 N:1, 일반게시판 게시물(board_post)에 N:1로 연관된다. (N:N의 중간)
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, TagMapstruct.class})
public interface ContentTagMapstruct
        extends BaseMapstruct<ContentTagDto, ContentTagEntity> {

    ContentTagMapstruct INSTANCE = Mappers.getMapper(ContentTagMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "tag", expression = "java(TagMapstruct.INSTANCE.toDto(entity.getTag()))")
    ContentTagDto toDto(final ContentTagEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    // @Mapping(target = "tag", expression = "java(TagMapstruct.INSTANCE.toEntity(dto.getTag()))")
    ContentTagEntity toEntity(final ContentTagDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final ContentTagDto dto,
            final @MappingTarget ContentTagEntity entity
    ) throws Exception;
}
