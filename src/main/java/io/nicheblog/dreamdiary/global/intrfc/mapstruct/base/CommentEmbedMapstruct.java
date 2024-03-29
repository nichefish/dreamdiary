package io.nicheblog.dreamdiary.global.intrfc.mapstruct.base;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.CommentEmbed;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * BoardPostMapstruct
 * <pre>
 *  일반게시판 게시물 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface CommentEmbedMapstruct
        extends BaseMapstruct<CommentCmpstn, CommentEmbed> {

    CommentEmbedMapstruct INSTANCE = Mappers.getMapper(CommentEmbedMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "commentList", expression = "java(entity.getCommentDtoList())")
    CommentCmpstn toDto(final CommentEmbed entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "commentList", expression = "java(dto.getCommentEntityList())")
    CommentEmbed toEntity(final CommentCmpstn dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final CommentCmpstn dto,
            final @MappingTarget CommentEmbed entity
    ) throws Exception;
}
