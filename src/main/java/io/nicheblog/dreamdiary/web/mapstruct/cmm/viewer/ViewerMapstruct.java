package io.nicheblog.dreamdiary.web.mapstruct.cmm.viewer;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.viewer.ViewerEntity;
import io.nicheblog.dreamdiary.web.model.cmm.viewer.ViewerDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ViewerMapstruct
 * <pre>
 *  일반게시판 게시물 열람자 MapStruct 기반 Mapper 인터페이스
 *  일반게시판 게시물 열람자(Viewer) = 일반게시판 게시물()에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface ViewerMapstruct
        extends BaseMapstruct<ViewerDto, ViewerEntity> {

    ViewerMapstruct INSTANCE = Mappers.getMapper(ViewerMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    ViewerDto toDto(final ViewerEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "postNo", expression = "java(StringUtils.isNotEmpty(dto.getPostNo()) ? Integer.parseInt(dto.getPostNo()) : null)")
    ViewerEntity toEntity(final ViewerDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final ViewerDto dto,
            final @MappingTarget ViewerEntity entity
    ) throws Exception;
}
