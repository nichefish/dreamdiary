package io.nicheblog.dreamdiary.domain.jrnl.day.mapstruct;

import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDayEntity;
import io.nicheblog.dreamdiary.domain.jrnl.day.model.JrnlDayApiDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlDayApiMapstruct
 * <pre>
 *  API:: 저널 일자 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface JrnlDayApiMapstruct
        extends BaseCrudMapstruct<JrnlDayApiDto, JrnlDayApiDto, JrnlDayEntity> {

    JrnlDayApiMapstruct INSTANCE = Mappers.getMapper(JrnlDayApiMapstruct.class);

    /**
     * Entity -> Dto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return Dto -- 변환된 Dto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toDto")
    @Mapping(target = "jrnlDt", expression = "java(DateUtils.asStr(entity.getJrnlDt(), DatePtn.DATE))")
    JrnlDayApiDto toDto(final JrnlDayEntity entity) throws Exception;

    /**
     * Entity -> ListDto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return ListDto -- 변환된 ListDto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "jrnlDt", expression = "java(DateUtils.asStr(entity.getJrnlDt(), DatePtn.DATE))")
    JrnlDayApiDto toListDto(final JrnlDayEntity entity) throws Exception;

    /**
     * Dto -> Entity 변환
     *
     * @param dto 변환할 Dto 객체
     * @return Entity -- 변환된 Entity 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Mapping(target = "jrnlDt", expression = "java(DateUtils.asDate(dto.getJrnlDt()))")
    JrnlDayEntity toEntity(final JrnlDayApiDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     *
     * @param dto 업데이트할 DTO 객체
     * @param entity 업데이트할 대상 엔티티 객체
     * @throws Exception 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final JrnlDayApiDto dto, final @MappingTarget JrnlDayEntity entity) throws Exception;
}
