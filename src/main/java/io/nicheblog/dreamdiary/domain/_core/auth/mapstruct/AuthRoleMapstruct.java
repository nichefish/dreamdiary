package io.nicheblog.dreamdiary.domain._core.auth.mapstruct;

import io.nicheblog.dreamdiary.domain._core.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.domain._core.auth.model.AuthRoleDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * AuthRoleMapstruct
 * <pre>
 *  권한 정보 관리 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface AuthRoleMapstruct
        extends BaseCrudMapstruct<AuthRoleDto, AuthRoleDto, AuthRoleEntity> {

    AuthRoleMapstruct INSTANCE = Mappers.getMapper(AuthRoleMapstruct.class);

    /**
     * Entity -> Dto
     * @param entity - 변환할 Entity 객체
     * @return Dto - 변환된 Dto 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toDto")
    AuthRoleDto toDto(final AuthRoleEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     * @param entity - 변환할 Entity 객체
     * @return ListDto - 변환된 ListDto 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toListDto")
    AuthRoleDto toListDto(final AuthRoleEntity entity) throws Exception;

    /**
     * Dto -> Entity
     * @param dto - 변환할 AtchFileDtlDto 객체
     * @return Entity - 변환된 AtchFileDtlEntity 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    AuthRoleEntity toEntity(final AuthRoleDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     * @param dto - 업데이트할 DTO 객체
     * @param entity - 업데이트할 대상 엔티티 객체
     * @throws Exception - 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final AuthRoleDto dto, final @MappingTarget AuthRoleEntity entity) throws Exception;
}
