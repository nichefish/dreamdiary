package io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbed;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * CommentEmbedMapstruct
 * <pre>
 *  댓글 모듈 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface CommentEmbedMapstruct
        extends BaseMapstruct<CommentCmpstn, CommentEmbed> {

    CommentEmbedMapstruct INSTANCE = Mappers.getMapper(CommentEmbedMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "list", expression = "java(entity.getDtoList())")
    CommentCmpstn toDto(final CommentEmbed entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "list", expression = "java(dto.getEntityList())")
    CommentEmbed toEntity(final CommentCmpstn dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final CommentCmpstn dto, final @MappingTarget CommentEmbed entity) throws Exception;
}
