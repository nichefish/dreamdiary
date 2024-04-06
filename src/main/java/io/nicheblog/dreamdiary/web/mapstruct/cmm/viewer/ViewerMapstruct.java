package io.nicheblog.dreamdiary.web.mapstruct.cmm.viewer;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.MapstructHelper;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.viewer.ViewerEntity;
import io.nicheblog.dreamdiary.web.model.cmm.viewer.ViewerDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ViewerMapstruct
 * <pre>
 *  컨텐츠 게시물 열람자 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class})
public interface ViewerMapstruct
        extends BaseMapstruct<ViewerDto, ViewerEntity> {

    ViewerMapstruct INSTANCE = Mappers.getMapper(ViewerMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DatePtn.DATETIME))")
    @Mapping(target = "regstrNm", expression = "java(entity.getRegstrInfo() != null ? entity.getRegstrInfo().getNickNm() : null)")
    ViewerDto toDto(final ViewerEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    ViewerEntity toEntity(final ViewerDto dto) throws Exception;

    /**
     * default : BaseEntity 기본 요소들 매핑
     */
    @AfterMapping
    default void mapBaseFields(final ViewerEntity entity, final @MappingTarget ViewerDto dto) throws Exception {
        MapstructHelper.mapBaseFields(entity, dto);
    }
}
