package io.nicheblog.dreamdiary.web.mapstruct.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagSmpEntity;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * TagSmpMapstruct
 * <pre>
 *  일반게시판 태그 MapStruct 기반 Mapper 인터페이스
 *  ※일반게시판 태그(board_tag) = 일반게시판별 태그 정보. 일반게시판 게시물 태그(board_post_tag)와 1:N으로 연관된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {})
public interface TagSmpMapstruct
        extends BaseListMapstruct<TagDto, TagDto, TagSmpEntity> {

    TagSmpMapstruct INSTANCE = Mappers.getMapper(TagSmpMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    TagDto toDto(final TagSmpEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    TagDto toListDto(final TagSmpEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    TagSmpEntity toEntity(final TagDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final TagDto dto,
            final @MappingTarget TagSmpEntity entity
    ) throws Exception;
}
