package io.nicheblog.dreamdiary.web.mapstruct.jrnl.diary;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlDiaryTagMapstruct
 * <pre>
 *  저널 일기 태그 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {ContentTagMapstruct.class})
public interface JrnlDiaryTagMapstruct
        extends BaseCrudMapstruct<TagDto, TagDto, JrnlDiaryTagEntity> {

    JrnlDiaryTagMapstruct INSTANCE = Mappers.getMapper(JrnlDiaryTagMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    TagDto toDto(final JrnlDiaryTagEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    TagDto toListDto(final JrnlDiaryTagEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    JrnlDiaryTagEntity toEntity(final TagDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final TagDto dto, final @MappingTarget JrnlDiaryTagEntity entity) throws Exception;
}
