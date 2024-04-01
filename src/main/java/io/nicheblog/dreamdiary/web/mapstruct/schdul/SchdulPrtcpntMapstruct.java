package io.nicheblog.dreamdiary.web.mapstruct.schdul;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulPrtcpntEntity;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulPrtcpntDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * SchdulPrtcpntMapstruct
 * <pre>
 *  일정 참여자 MapStruct 기반 Mapper 인터페이스
 *  일정 참여자(SchdulPrtcpnt) = 일정(Schdul)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DateUtils.class})
public interface SchdulPrtcpntMapstruct
        extends BaseMapstruct<SchdulPrtcpntDto, SchdulPrtcpntEntity> {

    SchdulPrtcpntMapstruct INSTANCE = Mappers.getMapper(SchdulPrtcpntMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "userNm", expression = "java((entity.getUser() != null) ? entity.getUser().getNickNm() : null)")
    SchdulPrtcpntDto toDto(final SchdulPrtcpntEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    SchdulPrtcpntEntity toEntity(final SchdulPrtcpntDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final SchdulPrtcpntDto dto,
            final @MappingTarget SchdulPrtcpntEntity entity
    ) throws Exception;
}
