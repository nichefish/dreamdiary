package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCd;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ClCdMapstruct
 * <pre>
 *  분류코드 관리 MapStruct 기반 Mapper 인터페이스
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface ClCdMapstruct
        extends BaseCrudMapstruct<ClCd, ClCd, ClCdEntity> {

    ClCdMapstruct INSTANCE = Mappers.getMapper(ClCdMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "dtlCdList", expression = "java(entity.getDtlCdDtoList())")
    ClCd toDto(final ClCdEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    ClCd toListDto(final ClCdEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "dtlCdList", expression = "java(dto.getDtlCdEntityList())")
    ClCdEntity toEntity(final ClCd dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "dtlCdList", expression = "java(dto.getDtlCdEntityList())")
    void updateFromDto(final ClCd dto, final @MappingTarget ClCdEntity entity) throws Exception;
}
