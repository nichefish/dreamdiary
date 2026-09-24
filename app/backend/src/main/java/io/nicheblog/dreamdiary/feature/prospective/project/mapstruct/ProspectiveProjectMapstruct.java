package io.nicheblog.dreamdiary.feature.prospective.project.mapstruct;

import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.feature.prospective.project.model.ProspectiveProjectDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseReadMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseWriteMapstruct;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/** Prospective Project Entity ↔ DTO 매핑 계약. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface ProspectiveProjectMapstruct
        extends BaseWriteMapstruct<ProspectiveProjectDto, ProspectiveProjectEntity>,
                BaseReadMapstruct<ProspectiveProjectDto, ProspectiveProjectEntity> {

    ProspectiveProjectMapstruct INSTANCE = Mappers.getMapper(ProspectiveProjectMapstruct.class);

    @Override
    @Named("toDto")
    ProspectiveProjectDto toDto(final ProspectiveProjectEntity entity) throws Exception;

    @Override
    ProspectiveProjectEntity toEntity(final ProspectiveProjectDto dto) throws Exception;

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final ProspectiveProjectDto dto, final @MappingTarget ProspectiveProjectEntity entity) throws Exception;
}
