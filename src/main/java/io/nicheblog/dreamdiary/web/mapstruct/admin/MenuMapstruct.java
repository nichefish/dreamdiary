package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.admin.MenuEntity;
import io.nicheblog.dreamdiary.web.model.admin.MenuDto;
import io.nicheblog.dreamdiary.web.model.admin.MenuListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * MenuMapstruct
 * <pre>
 *  메뉴 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface MenuMapstruct
        extends BaseListMapstruct<MenuDto, MenuListDto, MenuEntity> {

    MenuMapstruct INSTANCE = Mappers.getMapper(MenuMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "subMenuList", expression = "java(entity.getSubMenuDtoList())")
    MenuDto toDto(final MenuEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    MenuEntity toEntity(final MenuDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final MenuDto dto,
            final @MappingTarget MenuEntity entity
    ) throws Exception;
}
