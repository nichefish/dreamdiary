package io.nicheblog.dreamdiary.web.mapstruct.prjct;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.prjct.PrjctEntity;
import io.nicheblog.dreamdiary.web.model.prjct.PrjctDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * PrjctMapstruct
 * <pre>
 *  프로젝트 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface PrjctMapstruct
        extends BaseMapstruct<PrjctDto, PrjctEntity> {

    PrjctMapstruct INSTANCE = Mappers.getMapper(PrjctMapstruct.class);

    /**
     * Entity -> Dto
     */
    PrjctDto toDto(final PrjctEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    PrjctEntity toEntity(final PrjctDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final PrjctDto dto,
            final @MappingTarget PrjctEntity entity
    ) throws Exception;
}
