package io.nicheblog.dreamdiary.web.mapstruct.jrnl.sumry;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.sumry.JrnlSumryDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlSumryMapstruct
 * <pre>
 *  저널 결산 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface JrnlSumryMapstruct
        extends BaseClsfMapstruct<JrnlSumryDto.DTL, JrnlSumryDto.LIST, JrnlSumryEntity> {

    JrnlSumryMapstruct INSTANCE = Mappers.getMapper(JrnlSumryMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "sumryCnDiaryList", expression = "java(JrnlSumryCnMapstruct.INSTANCE.toDtoList(entity.getSumryCnDiaryList()))")
    @Mapping(target = "sumryCnDreamList", expression = "java(JrnlSumryCnMapstruct.INSTANCE.toDtoList(entity.getSumryCnDreamList()))")
    JrnlSumryDto.DTL toDto(final JrnlSumryEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    JrnlSumryDto.LIST toListDto(final JrnlSumryEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    JrnlSumryEntity toEntity(final JrnlSumryDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final JrnlSumryDto.DTL dto, final @MappingTarget JrnlSumryEntity entity) throws Exception;
}
