package io.nicheblog.dreamdiary.web.mapstruct.cmm.sectn;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.SectnEmbedMapstruct;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.sectn.SectnEntity;
import io.nicheblog.dreamdiary.web.model.cmm.sectn.SectnDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * SectnMapstruct
 * <pre>
 *  단락 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CmmUtils.class, SectnEmbedMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface SectnMapstruct
        extends BasePostMapstruct<SectnDto, SectnDto, SectnEntity> {

    SectnMapstruct INSTANCE = Mappers.getMapper(SectnMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    SectnDto toDto(final SectnEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    SectnDto toListDto(final SectnEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    SectnEntity toEntity(final SectnDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final SectnDto dto, final @MappingTarget SectnEntity entity) throws Exception;
}
