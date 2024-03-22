package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import io.nicheblog.dreamdiary.web.model.admin.LgnPolicyDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * LgnPolicyMapstruct
 * <pre>
 *  로그인 정책 관리 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LgnPolicyMapstruct
        extends BaseMapstruct<LgnPolicyDto, LgnPolicyEntity> {

    LgnPolicyMapstruct INSTANCE = Mappers.getMapper(LgnPolicyMapstruct.class);

    /**
     * Dto -> Entity
     */
    @Override
    LgnPolicyEntity toEntity(final LgnPolicyDto dto) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    LgnPolicyDto toDto(final LgnPolicyEntity entity) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final LgnPolicyDto dto,
            final @MappingTarget LgnPolicyEntity entity
    ) throws Exception;
}
