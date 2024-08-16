package io.nicheblog.dreamdiary.web.mapstruct.jrnl.day;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlDayTagMapstruct
 * <pre>
 *  저널 일자 태그 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {ContentTagMapstruct.class})
public interface JrnlDayTagMapstruct
        extends BaseCrudMapstruct<TagDto, TagDto, JrnlDayTagEntity> {

    JrnlDayTagMapstruct INSTANCE = Mappers.getMapper(JrnlDayTagMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    TagDto toDto(final JrnlDayTagEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    TagDto toListDto(final JrnlDayTagEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    JrnlDayTagEntity toEntity(final TagDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final TagDto dto, final @MappingTarget JrnlDayTagEntity entity) throws Exception;
}
