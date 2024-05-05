package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.admin.MenuEntity;
import io.nicheblog.dreamdiary.web.model.admin.MenuDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * MenuMapstruct
 * <pre>
 *  메뉴 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface MenuMapstruct
        extends BaseCrudMapstruct<MenuDto, MenuDto, MenuEntity> {

    MenuMapstruct INSTANCE = Mappers.getMapper(MenuMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "subMenuList", expression = "java(toDtoList(entity.getSubMenuList()))")
    @Mapping(target = "upperMenuNm", expression = "java(entity.getUpperMenu() != null ? entity.getUpperMenu().getMenuNm() : null)")
    MenuDto toDto(final MenuEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "subMenuList", expression = "java(toDtoList(entity.getSubMenuList()))")
    @Mapping(target = "upperMenuNm", expression = "java(entity.getUpperMenu() != null ? entity.getUpperMenu().getMenuNm() : null)")
    MenuDto toListDto(final MenuEntity entity) throws Exception;

    /**
     * EntityList to DtoList
     */
    default List<MenuDto> toDtoList(List<MenuEntity> entityList) {
        AtomicLong i = new AtomicLong(1);
        return entityList.stream()
                .map(entity -> {
                    try {
                        return this.toDto(entity);
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
    MenuEntity toEntity(final MenuDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final MenuDto dto, final @MappingTarget MenuEntity entity) throws Exception;
}
