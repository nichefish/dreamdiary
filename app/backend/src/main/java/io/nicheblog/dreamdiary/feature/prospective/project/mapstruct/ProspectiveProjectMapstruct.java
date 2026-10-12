package io.nicheblog.dreamdiary.feature.prospective.project.mapstruct;

import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.feature.prospective.project.model.ProspectiveProjectDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseReadMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseWriteMapstruct;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Prospective Project Entity ↔ DTO 매핑 계약.
 * <p>
 * 쓰기(toEntity·updateFromDto)는 감사 필드(created/updated by·at·info)를 요청 DTO에서 복사하지 않는다.
 * 감사 값은 JPA Auditing이 채우며, 응답 형식("yyyy-MM-dd HH:mm:ss") 그대로 되돌아온 문자열을
 * LocalDateTime.parse 하다 500이 나거나 작성자 표시가 요청값으로 바뀌는 것을 막는다.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface ProspectiveProjectMapstruct
        extends BaseWriteMapstruct<ProspectiveProjectDto, ProspectiveProjectEntity>,
                BaseReadMapstruct<ProspectiveProjectDto, ProspectiveProjectEntity> {

    ProspectiveProjectMapstruct INSTANCE = Mappers.getMapper(ProspectiveProjectMapstruct.class);

    @Override
    @Named("toDto")
    ProspectiveProjectDto toDto(final ProspectiveProjectEntity entity) throws Exception;

    @Override
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdByInfo", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedByInfo", ignore = true)
    ProspectiveProjectEntity toEntity(final ProspectiveProjectDto dto) throws Exception;

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdByInfo", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedByInfo", ignore = true)
    void updateFromDto(final ProspectiveProjectDto dto, final @MappingTarget ProspectiveProjectEntity entity) throws Exception;
}
