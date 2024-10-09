package io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.ManagtCmpstn;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ManagtEmbedMapstruct
 * <pre>
 *  조치 모듈 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface ManagtEmbedMapstruct
        extends BaseMapstruct<ManagtCmpstn, ManagtEmbed> {

    ManagtEmbedMapstruct INSTANCE = Mappers.getMapper(ManagtEmbedMapstruct.class);

    /**
     * Entity -> Dto
     * @param entity - 변환할 Entity 객체
     * @return Dto - 변환된 Dto 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    @Mapping(target = "list", expression = "java(entity.getDtoList())")
    @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DatePtn.DATETIME))")
    ManagtCmpstn toDto(final ManagtEmbed entity) throws Exception;

    /**
     * Dto -> Entity
     * @param dto - 변환할 Dto 객체
     * @return Entity - 변환된 Entity 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    @Mapping(target = "list", expression = "java(dto.getEntityList())")
    @Mapping(target = "managtDt", expression = "java(DateUtils.asDate(dto.getManagtDt()))")
    ManagtEmbed toEntity(final ManagtCmpstn dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     * @param dto - 업데이트할 DTO 객체
     * @param entity - 업데이트할 대상 엔티티 객체
     * @throws Exception - 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final ManagtCmpstn dto, final @MappingTarget ManagtEmbed entity) throws Exception;
}
