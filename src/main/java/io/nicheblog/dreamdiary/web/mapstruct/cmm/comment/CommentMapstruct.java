package io.nicheblog.dreamdiary.web.mapstruct.cmm.comment;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.CommentEmbedMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * CommentMapstruct
 * <pre>
 *  일반게시판 댓글 MapStruct 기반 Mapper 인터페이스
 *  ※일반게시판 댓글(board_comment) = 일반게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CommentEmbedMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface CommentMapstruct
        extends io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct<CommentDto, CommentListDto, CommentEntity> {

    CommentMapstruct INSTANCE = Mappers.getMapper(CommentMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    CommentDto toDto(final CommentEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    CommentListDto toListDto(final CommentEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    CommentEntity toEntity(final CommentDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final CommentDto dto,
            final @MappingTarget CommentEntity entity
    ) throws Exception;
}
