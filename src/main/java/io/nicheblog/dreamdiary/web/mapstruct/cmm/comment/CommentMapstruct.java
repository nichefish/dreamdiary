package io.nicheblog.dreamdiary.web.mapstruct.cmm.comment;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseAuditListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * CommentMapstruct
 * <pre>
 *  게시판 댓글 MapStruct 기반 Mapper 인터페이스
 *  ※게시판 댓글(board_comment) = 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface CommentMapstruct
        extends BaseAuditListMapstruct<CommentDto, CommentDto, CommentEntity> {

    CommentMapstruct INSTANCE = Mappers.getMapper(CommentMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "commentList", expression = "java(entity.getCommentDtoList())")
    CommentDto toDto(final CommentEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "commentList", expression = "java(entity.getCommentDtoList())")
    CommentDto toListDto(final CommentEntity entity) throws Exception;

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
