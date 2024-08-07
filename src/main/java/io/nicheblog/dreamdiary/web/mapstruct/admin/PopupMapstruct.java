package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
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
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface PopupMapstruct
        extends BaseCrudMapstruct<PopupDto, PopupDto, PopupEntity> {

    PopupMapstruct INSTANCE = Mappers.getMapper(PopupMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "popupStartDt", expression = "java(DateUtils.asStr(entity.getPopupStartDt(), DatePtn.DATETIME))")
    @Mapping(target = "popupEndDt", expression = "java(DateUtils.asStr(entity.getPopupEndDt(), DatePtn.DATETIME))")
    PopupDto toDto(final PopupEntity entity) throws Exception;


    /**
     * Entity -> Dto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "popupStartDt", expression = "java(DateUtils.asStr(entity.getPopupStartDt(), DatePtn.DATETIME))")
    @Mapping(target = "popupEndDt", expression = "java(DateUtils.asStr(entity.getPopupEndDt(), DatePtn.DATETIME))")
    PopupDto toListDto(final PopupEntity entity) throws Exception;

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
    void updateFromDto(final PopupDto dto, final @MappingTarget PopupEntity entity) throws Exception;
}
