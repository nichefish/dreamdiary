package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseAuditListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.admin.PopupEntity;
import io.nicheblog.dreamdiary.web.model.admin.PopupDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * PopupMapstruct
 * <pre>
 *  팝업 정보 관리 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface PopupMapstruct
        extends BaseAuditListMapstruct<PopupDto, PopupDto, PopupEntity> {

    PopupMapstruct INSTANCE = Mappers.getMapper(PopupMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "popupStartDt", expression = "java(DateUtils.asStr(entity.getPopupStartDt(), DateUtils.PTN_DATETIME))")
    @Mapping(target = "popupEndDt", expression = "java(DateUtils.asStr(entity.getPopupEndDt(), DateUtils.PTN_DATETIME))")
    PopupDto toDto(final PopupEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "popupStartDt", expression = "java(DateUtils.asDate(dto.getPopupStartDt()))")
    @Mapping(target = "popupEndDt", expression = "java(DateUtils.asDate(dto.getPopupEndDt()))")
    PopupEntity toEntity(final PopupDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "popupStartDt", expression = "java(DateUtils.asDate(dto.getPopupStartDt()))")
    @Mapping(target = "popupEndDt", expression = "java(DateUtils.asDate(dto.getPopupEndDt()))")
    void updateFromDto(
            final PopupDto dto,
            final @MappingTarget PopupEntity entity
    ) throws Exception;
}
