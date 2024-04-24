package io.nicheblog.dreamdiary.web.mapstruct.jrnl.summary;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryCnEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.sumry.JrnlSumryCnDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlSumryCnMapstruct
 * <pre>
 *  저널 결산 내용 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class}, builder = @Builder(disableBuilder = true))
public interface JrnlSumryCnMapstruct
        extends BaseClsfMapstruct<JrnlSumryCnDto, JrnlSumryCnDto, JrnlSumryCnEntity> {

    JrnlSumryCnMapstruct INSTANCE = Mappers.getMapper(JrnlSumryCnMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    JrnlSumryCnDto toDto(final JrnlSumryCnEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    JrnlSumryCnDto toListDto(final JrnlSumryCnEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    JrnlSumryCnEntity toEntity(final JrnlSumryCnDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final JrnlSumryCnDto dto, final @MappingTarget JrnlSumryCnEntity entity) throws Exception;
}
