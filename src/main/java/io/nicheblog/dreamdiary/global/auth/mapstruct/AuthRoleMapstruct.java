package io.nicheblog.dreamdiary.global.auth.mapstruct;

import io.nicheblog.dreamdiary.global.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.global.auth.model.AuthRoleDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * AuthRoleMapstruct
 * <pre>
 *  권한 정보 관리 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface AuthRoleMapstruct
        extends BaseCrudMapstruct<AuthRoleDto, AuthRoleDto, AuthRoleEntity> {

    AuthRoleMapstruct INSTANCE = Mappers.getMapper(AuthRoleMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    AuthRoleDto toDto(final AuthRoleEntity entity) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toListDto")
    AuthRoleDto toListDto(final AuthRoleEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    AuthRoleEntity toEntity(final AuthRoleDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final AuthRoleDto dto,
            final @MappingTarget AuthRoleEntity entity
    ) throws Exception;
}
