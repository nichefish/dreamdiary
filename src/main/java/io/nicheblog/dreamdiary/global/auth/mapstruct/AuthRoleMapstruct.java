package io.nicheblog.dreamdiary.global.auth.mapstruct;

import io.nicheblog.dreamdiary.global.auth.entity.AuthRole;
import io.nicheblog.dreamdiary.global.auth.model.AuthRoleDto;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCd;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
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
        extends BaseListMapstruct<AuthRoleDto, AuthRoleDto, AuthRole> {

    AuthRoleMapstruct INSTANCE = Mappers.getMapper(AuthRoleMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "dtlCdList", expression = "java(entity.getDtlCdDtoList())")
    AuthRoleDto toDto(final AuthRole entity) throws Exception;

    /**
     * Entity -> listDto
     */
    AuthRoleDto toListDto(final AuthRole entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "dtlCdList", expression = "java(dto.getDtlCdEntityList())")
    AuthRole toEntity(final AuthRoleDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "dtlCdList", expression = "java(dto.getDtlCdEntityList())")
    void updateFromDto(
            final AuthRoleDto dto,
            final @MappingTarget AuthRole entity
    ) throws Exception;
}
