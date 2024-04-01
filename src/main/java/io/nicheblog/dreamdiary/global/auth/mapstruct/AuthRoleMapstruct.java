package io.nicheblog.dreamdiary.global.auth.mapstruct;

import io.nicheblog.dreamdiary.global.auth.entity.AuthRole;
import io.nicheblog.dreamdiary.global.auth.model.AuthRoleDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * AuthRoleMapstruct
 * <pre>
 *  분류코드 관리 MapStruct 기반 Mapper 인터페이스
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface AuthRoleMapstruct
        extends BaseMapstruct<AuthRoleDto, AuthRole> {

    AuthRoleMapstruct INSTANCE = Mappers.getMapper(AuthRoleMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    AuthRoleDto toDto(final AuthRole entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    AuthRole toEntity(final AuthRoleDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final AuthRoleDto dto,
            final @MappingTarget AuthRole entity
    ) throws Exception;
}
