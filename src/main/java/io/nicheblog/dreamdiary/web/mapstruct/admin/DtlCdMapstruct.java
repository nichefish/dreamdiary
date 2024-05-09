package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCdDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * DtlCdMapstruct
 * <pre>
 *  상세코드 관리 MapStruct 기반 Mapper 인터페이스
 *  ※상세코드(dtl_cd) = 분류코드 하위의 상세코드. 분류코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DtlCdMapstruct
        extends BaseCrudMapstruct<DtlCdDto, DtlCdDto, DtlCdEntity> {

    DtlCdMapstruct INSTANCE = Mappers.getMapper(DtlCdMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    DtlCdDto toDto(final DtlCdEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    DtlCdDto toListDto(final DtlCdEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    DtlCdEntity toEntity(final DtlCdDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final DtlCdDto dto, final @MappingTarget DtlCdEntity entity) throws Exception;
}
