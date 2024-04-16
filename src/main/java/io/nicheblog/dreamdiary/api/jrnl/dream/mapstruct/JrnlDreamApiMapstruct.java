package io.nicheblog.dreamdiary.api.jrnl.dream.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlDreamMapstruct
 * <pre>
 *  API:: 저널 꿈 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface JrnlDreamApiMapstruct
        extends BaseCrudMapstruct<JrnlDreamDto, JrnlDreamDto, JrnlDreamEntity> {

    JrnlDreamApiMapstruct INSTANCE = Mappers.getMapper(JrnlDreamApiMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    JrnlDreamDto toDto(final JrnlDreamEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
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
