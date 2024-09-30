package io.nicheblog.dreamdiary.web.mapstruct.board;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.CommentEmbedMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.ManagtEmbedMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.TagEmbedMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.ViewerEmbedMapstruct;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.board.BoardPostEntity;
import io.nicheblog.dreamdiary.web.entity.board.BoardPostSmpEntity;
import io.nicheblog.dreamdiary.web.model.board.BoardPostDto;
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
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CmmUtils.class, CommentEmbedMapstruct.class, ViewerEmbedMapstruct.class, ManagtEmbedMapstruct.class, TagEmbedMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface BoardPostMapstruct
        extends BaseClsfMapstruct<BoardPostDto.DTL, BoardPostDto.LIST, BoardPostEntity> {

    BoardPostMapstruct INSTANCE = Mappers.getMapper(BoardPostMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "boardCd", source = "contentType")
    @Mapping(target = "ctgrClCd", expression = "java((entity.getBoardDefInfo() != null) ? entity.getBoardDefInfo().getCtgrClCd() : null)")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    BoardPostDto.DTL toDto(final BoardPostEntity entity) throws Exception;

    /**
     * Entity -> Dto
     */
    @Mapping(target = "boardCd", source = "contentType")
    @Mapping(target = "ctgrClCd", expression = "java((entity.getBoardDefInfo() != null) ? entity.getBoardDefInfo().getCtgrClCd() : null)")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    BoardPostDto.DTL toDto(final BoardPostSmpEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "boardCd", source = "contentType")
    @Mapping(target = "ctgrClCd", expression = "java((entity.getBoardDefInfo() != null) ? entity.getBoardDefInfo().getCtgrClCd() : null)")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    BoardPostDto.LIST toListDto(final BoardPostEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "contentType", source = "boardCd")
    BoardPostEntity toEntity(final BoardPostDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @Mapping(target = "contentType", source = "boardCd")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final BoardPostDto.DTL dto, final @MappingTarget BoardPostEntity entity) throws Exception;
}
