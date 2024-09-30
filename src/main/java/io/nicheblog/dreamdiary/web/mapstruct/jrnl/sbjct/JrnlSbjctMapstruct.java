package io.nicheblog.dreamdiary.web.mapstruct.jrnl.sbjct;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.sbjct.JrnlSbjctEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.sbjct.JrnlSbjctDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlSbjctMapstruct
 * <pre>
 *  공지사항 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CmmUtils.class}, builder = @Builder(disableBuilder = true))
public interface JrnlSbjctMapstruct
        extends BasePostMapstruct<JrnlSbjctDto.DTL, JrnlSbjctDto.LIST, JrnlSbjctEntity> {

    JrnlSbjctMapstruct INSTANCE = Mappers.getMapper(JrnlSbjctMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    JrnlSbjctDto.DTL toDto(final JrnlSbjctEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    JrnlSbjctDto.LIST toListDto(final JrnlSbjctEntity entity) throws Exception;

    /**
     * Entity -> XlsxDto
     */
    // @Named("toXlsxDto")
    // JrnlSbjctXlsxDto toXlsxDto(final JrnlSbjctEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    JrnlSbjctEntity toEntity(final JrnlSbjctDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final JrnlSbjctDto.DTL dto, final @MappingTarget JrnlSbjctEntity entity) throws Exception;
}
