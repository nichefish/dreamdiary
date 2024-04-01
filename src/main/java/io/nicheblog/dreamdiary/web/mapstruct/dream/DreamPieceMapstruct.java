package io.nicheblog.dreamdiary.web.mapstruct.dream;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfListMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.dream.DreamPieceEntity;
import io.nicheblog.dreamdiary.web.model.dream.piece.DreamPieceDto;
import io.nicheblog.dreamdiary.web.model.dream.piece.DreamPieceListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DreamPieceMapstruct
 * <pre>
 *  꿈 일자 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface DreamPieceMapstruct
        extends BaseClsfListMapstruct<DreamPieceDto, DreamPieceListDto, DreamPieceEntity> {

    DreamPieceMapstruct INSTANCE = Mappers.getMapper(DreamPieceMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    DreamPieceDto toDto(final DreamPieceEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    DreamPieceListDto toListDto(final DreamPieceEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    DreamPieceEntity toEntity(final DreamPieceDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final DreamPieceDto dto,
            final @MappingTarget DreamPieceEntity entity
    ) throws Exception;
}
