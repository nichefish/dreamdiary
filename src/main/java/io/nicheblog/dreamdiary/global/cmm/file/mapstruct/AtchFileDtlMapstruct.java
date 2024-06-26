package io.nicheblog.dreamdiary.global.cmm.file.mapstruct;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * AtchFileDtlMapstruct
 * <pre>
 *  첨부파일 상세 MapStruct 기반 Mapper 인터페이스
 *  ※첨부파일 상세(atch_file_dtl) = 실제 첨부파일 정보를 담고 있는 객체. 첨부파일(atch_file)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AtchFileDtlMapstruct
        extends BaseCrudMapstruct<AtchFileDtlDto, AtchFileDtlDto, AtchFileDtlEntity> {

    AtchFileDtlMapstruct INSTANCE = Mappers.getMapper(AtchFileDtlMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    AtchFileDtlDto toDto(final AtchFileDtlEntity entity) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toListDto")
    AtchFileDtlDto toListDto(final AtchFileDtlEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    AtchFileDtlEntity toEntity(final AtchFileDtlDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final AtchFileDtlDto dto, final @MappingTarget AtchFileDtlEntity entity) throws Exception;
}
