package io.nicheblog.dreamdiary.web.mapstruct.board;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.web.entity.board.BoardDefEntity;
import io.nicheblog.dreamdiary.web.model.board.BoardDefDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * BoardDefMapstruct
 * <pre>
 *  게시판 정의 MapStruct 기반 Mapper 인터페이스
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BoardDefMapstruct
        extends BaseListMapstruct<BoardDefDto, BoardDefDto, BoardDefEntity> {

    BoardDefMapstruct INSTANCE = Mappers.getMapper(BoardDefMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    BoardDefDto toDto(final BoardDefEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    BoardDefEntity toEntity(final BoardDefDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final BoardDefDto dto,
            final @MappingTarget BoardDefEntity entity
    ) throws Exception;
}
