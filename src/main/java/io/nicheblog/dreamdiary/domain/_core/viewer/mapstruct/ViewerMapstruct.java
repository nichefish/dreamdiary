package io.nicheblog.dreamdiary.domain._core.viewer.mapstruct;

import io.nicheblog.dreamdiary.domain._core.viewer.model.ViewerDto;
import io.nicheblog.dreamdiary.domain._core.viewer.entity.ViewerEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.MapstructHelper;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ViewerMapstruct
 * <pre>
 *  컨텐츠 열람자 MapStruct 기반 Mapper 인터페이스.
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
     * @param entity - 변환할 ViewerEntity 객체
     * @return 변환된 ViewerDto 객체
     * @throws Exception 변환 과정에서 발생할 수 있는 예외
     */
    @Override
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DatePtn.DATETIME))")
    @Mapping(target = "regstrNm", expression = "java(entity.getRegstrInfo() != null ? entity.getRegstrInfo().getNickNm() : null)")
    ViewerDto toDto(final ViewerEntity entity) throws Exception;

    /**
     * Dto -> Entity
     * @param dto - 변환할 ViewerDto 객체
     * @return 변환된 ViewerEntity 객체
     * @throws Exception - 변환 과정에서 발생할 수 있는 예외
     */
    @Override
    ViewerEntity toEntity(final ViewerDto dto) throws Exception;

    /**
     * default : BaseEntity 기본 요소들 매핑
     * @param entity - 매핑할 ViewerEntity 객체
     * @param dto - 매핑 대상인 ViewerDto 객체
     * @throws Exception 매핑 과정에서 발생할 수 있는 예외
     */
    @AfterMapping
    default void mapBaseFields(final ViewerEntity entity, final @MappingTarget ViewerDto dto) throws Exception {
        MapstructHelper.mapBaseFields(entity, dto);
    }
}
