package io.nicheblog.dreamdiary.web.mapstruct.jrnl.sumry;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryCnEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.sumry.JrnlSumryCnDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JrnlSumryCnMapstruct
 * <pre>
 *  저널 결산 내용 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class, CmmUtils.class}, builder = @Builder(disableBuilder = true))
public interface JrnlSumryCnMapstruct
        extends BasePostMapstruct<JrnlSumryCnDto, JrnlSumryCnDto, JrnlSumryCnEntity> {

    JrnlSumryCnMapstruct INSTANCE = Mappers.getMapper(JrnlSumryCnMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "markdownCn", expression = "java(CmmUtils.markdown(entity.getCn()))")
    JrnlSumryCnDto toDto(final JrnlSumryCnEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "markdownCn", expression = "java(CmmUtils.markdown(entity.getCn()))")
    JrnlSumryCnDto toListDto(final JrnlSumryCnEntity entity) throws Exception;

    /**
     * EntityList to DtoList
     */
    default List<JrnlSumryCnDto> toDtoList(List<JrnlSumryCnEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) return null;
        return entityList.stream()
                .map(entity -> {
                    try {
                        return JrnlSumryCnMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

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
