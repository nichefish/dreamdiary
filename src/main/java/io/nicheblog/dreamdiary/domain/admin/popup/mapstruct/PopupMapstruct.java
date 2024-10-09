package io.nicheblog.dreamdiary.domain.admin.popup.mapstruct;

import io.nicheblog.dreamdiary.domain.admin.popup.entity.PopupEntity;
import io.nicheblog.dreamdiary.domain.admin.popup.model.PopupDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * PopupMapstruct
 * <pre>
 *  팝업 정보 관리 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface PopupMapstruct
        extends BaseCrudMapstruct<PopupDto, PopupDto, PopupEntity> {

    PopupMapstruct INSTANCE = Mappers.getMapper(PopupMapstruct.class);

    /**
     * Entity -> Dto
     * @param entity - 변환할 Entity 객체
     * @return Dto - 변환된 Dto 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toDto")
    @Mapping(target = "popupStartDt", expression = "java(DateUtils.asStr(entity.getPopupStartDt(), DatePtn.DATETIME))")
    @Mapping(target = "popupEndDt", expression = "java(DateUtils.asStr(entity.getPopupEndDt(), DatePtn.DATETIME))")
    PopupDto toDto(final PopupEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     * @param entity - 변환할 Entity 객체
     * @return ListDto - 변환된 ListDto 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "popupStartDt", expression = "java(DateUtils.asStr(entity.getPopupStartDt(), DatePtn.DATETIME))")
    @Mapping(target = "popupEndDt", expression = "java(DateUtils.asStr(entity.getPopupEndDt(), DatePtn.DATETIME))")
    PopupDto toListDto(final PopupEntity entity) throws Exception;

    /**
     * Dto -> Entity
     * @param dto - 변환할 Dto 객체
     * @return Entity - 변환된 Entity 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    @Mapping(target = "popupStartDt", expression = "java(DateUtils.asDate(dto.getPopupStartDt()))")
    @Mapping(target = "popupEndDt", expression = "java(DateUtils.asDate(dto.getPopupEndDt()))")
    PopupEntity toEntity(final PopupDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     * @param dto - 업데이트할 DTO 객체
     * @param entity - 업데이트할 대상 엔티티 객체
     * @throws Exception - 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "popupStartDt", expression = "java(DateUtils.asDate(dto.getPopupStartDt()))")
    @Mapping(target = "popupEndDt", expression = "java(DateUtils.asDate(dto.getPopupEndDt()))")
    void updateFromDto(final PopupDto dto, final @MappingTarget PopupEntity entity) throws Exception;
}
