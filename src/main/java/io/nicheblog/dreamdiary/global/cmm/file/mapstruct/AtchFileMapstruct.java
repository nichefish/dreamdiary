package io.nicheblog.dreamdiary.global.cmm.file.mapstruct;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileEntity;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * AtchFileMapstruct
 * <pre>
 *  첨부파일 MapStruct 기반 Mapper 인터페이스
 *  ※첨부파일(atch_file) = 여러 첨부파일을 하나의 단위로 묶어놓은 객체. 첨부파일 상세(atch_file_dtl)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AtchFileMapstruct
        extends BaseCrudMapstruct<AtchFileDto, AtchFileDto, AtchFileEntity> {

    AtchFileMapstruct INSTANCE = Mappers.getMapper(AtchFileMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    AtchFileDto toDto(final AtchFileEntity entity) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toListDto")
    AtchFileDto toListDto(final AtchFileEntity entity) throws Exception;


    /**
     * Dto -> Entity
     */
    @Override
    AtchFileEntity toEntity(final AtchFileDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final AtchFileDto dto, final @MappingTarget AtchFileEntity entity) throws Exception;
}
