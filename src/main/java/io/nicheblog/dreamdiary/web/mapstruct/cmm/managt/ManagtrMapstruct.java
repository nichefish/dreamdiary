package io.nicheblog.dreamdiary.web.mapstruct.cmm.managt;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.managt.ManagtrEntity;
import io.nicheblog.dreamdiary.web.model.cmm.managtr.ManagtrDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ManagtrMapstruct
 * <pre>
 *  일반게시판 게시물 작업자 MapStruct 기반 Mapper 인터페이스
 *  게시물 게시물 작업자(managtr) = 일반게시판 수정이력자. 일반게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface ManagtrMapstruct
        extends BaseMapstruct<ManagtrDto, ManagtrEntity> {

    ManagtrMapstruct INSTANCE = Mappers.getMapper(ManagtrMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    ManagtrDto toDto(final ManagtrEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    ManagtrEntity toEntity(final ManagtrDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final ManagtrDto dto,
            final @MappingTarget ManagtrEntity entity
    ) throws Exception;
}
