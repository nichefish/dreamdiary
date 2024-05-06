package io.nicheblog.dreamdiary.web.mapstruct.jrnl.dream;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlDreamMapstruct
 * <pre>
 *  저널 꿈 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class, CmmUtils.class}, builder = @Builder(disableBuilder = true))
public interface JrnlDreamMapstruct
        extends BasePostMapstruct<JrnlDreamDto, JrnlDreamDto, JrnlDreamEntity> {

    JrnlDreamMapstruct INSTANCE = Mappers.getMapper(JrnlDreamMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "stdrdDt", expression = "java(DateUtils.asStr(\"Y\".equals(entity.getJrnlDay().getDtUnknownYn()) ? entity.getJrnlDay().getAprxmtDt() : entity.getJrnlDay().getJrnlDt(), DatePtn.DATE))")
    @Mapping(target = "markdownCn", expression = "java(CmmUtils.markdown(entity.getCn()))")
    JrnlDreamDto toDto(final JrnlDreamEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "stdrdDt", expression = "java(DateUtils.asStr(\"Y\".equals(entity.getJrnlDay().getDtUnknownYn()) ? entity.getJrnlDay().getAprxmtDt() : entity.getJrnlDay().getJrnlDt(), DatePtn.DATE))")
    @Mapping(target = "markdownCn", expression = "java(CmmUtils.markdown(entity.getCn()))")
    JrnlDreamDto toListDto(final JrnlDreamEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    JrnlDreamEntity toEntity(final JrnlDreamDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final JrnlDreamDto dto, final @MappingTarget JrnlDreamEntity entity) throws Exception;
}
