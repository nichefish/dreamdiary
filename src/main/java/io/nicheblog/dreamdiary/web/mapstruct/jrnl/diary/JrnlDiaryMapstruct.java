package io.nicheblog.dreamdiary.web.mapstruct.jrnl.diary;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.diary.JrnlDiaryDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlDiaryMapstruct
 * <pre>
 *  저널 일기 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class}, builder = @Builder(disableBuilder = true))
public interface JrnlDiaryMapstruct
        extends BaseClsfMapstruct<JrnlDiaryDto, JrnlDiaryDto, JrnlDiaryEntity> {

    JrnlDiaryMapstruct INSTANCE = Mappers.getMapper(JrnlDiaryMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "jrnlDt", expression = "java(DateUtils.asStr(\"Y\".equals(entity.getJrnlDay().getDtUnknownYn()) ? entity.getJrnlDay().getAprxmtDt() : entity.getJrnlDay().getJrnlDt(), DatePtn.DATE))")
    JrnlDiaryDto toDto(final JrnlDiaryEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "jrnlDt", expression = "java(DateUtils.asStr(\"Y\".equals(entity.getJrnlDay().getDtUnknownYn()) ? entity.getJrnlDay().getAprxmtDt() : entity.getJrnlDay().getJrnlDt(), DatePtn.DATE))")
    JrnlDiaryDto toListDto(final JrnlDiaryEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    JrnlDiaryEntity toEntity(final JrnlDiaryDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final JrnlDiaryDto dto, final @MappingTarget JrnlDiaryEntity entity) throws Exception;
}
