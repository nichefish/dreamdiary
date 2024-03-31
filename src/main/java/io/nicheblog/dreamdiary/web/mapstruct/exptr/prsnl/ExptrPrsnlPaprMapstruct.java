package io.nicheblog.dreamdiary.web.mapstruct.exptr.prsnl;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.base.CommentEmbedMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ExptrPrsnlPaprMapstruct
 * <pre>
 *  경비 관리 > 경비지출서 관리 MapStruct 기반 Mapper 인터페이스
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, ExptrPrsnlItemMapstruct.class, CommentEmbedMapstruct.class})
public interface ExptrPrsnlPaprMapstruct
        extends io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct<ExptrPrsnlPaprDto, ExptrPrsnlPaprListDto, ExptrPrsnlPaprEntity> {

    ExptrPrsnlPaprMapstruct INSTANCE = Mappers.getMapper(ExptrPrsnlPaprMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "itemList", expression = "java(entity.getItemDtoList())")
    @Mapping(target = "comment", expression = "java(CommentEmbedMapstruct.INSTANCE.toDto(entity.comment))")       // 댓글 모듈
    // @Mapping(target = "viewerList", expression = "java(entity.getViewerDtoList())")
    // @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DateUtils.PTN_DATETIME))")
    ExptrPrsnlPaprDto toDto(final ExptrPrsnlPaprEntity entity) throws Exception;

    /**
     * Entity -> listDto
     */
    @Override
    @Mapping(target = "comment", expression = "java(CommentEmbedMapstruct.INSTANCE.toDto(entity.comment))")       // 댓글 모듈
    ExptrPrsnlPaprListDto toListDto(final ExptrPrsnlPaprEntity entity) throws Exception;

    /**
     * StatsDto -> StatsXlsxDto
     */
    // @Mapping(target = "emplymNm", expression = "java(dto.getUser() != null ? dto.getUser().getEmplymNm() : \"\")")
    // @Mapping(target = "cmpyNm", expression = "java(dto.getUser() != null ? dto.getUser().getCmpyNm() : \"\")")
    // @Mapping(target = "userNm", expression = "java(dto.getUser() != null ? dto.getUser().getUserNm() : \"\")")
    // @Mapping(target = "jan", expression = "java(dto.getExptrPrsnlList().get(0) != null ? dto.getExptrPrsnlList().get(0).getTotAmt() : 0)")
    // @Mapping(target = "feb", expression = "java(dto.getExptrPrsnlList().get(1) != null ? dto.getExptrPrsnlList().get(1).getTotAmt() : 0)")
    // @Mapping(target = "mar", expression = "java(dto.getExptrPrsnlList().get(2) != null ? dto.getExptrPrsnlList().get(2).getTotAmt() : 0)")
    // @Mapping(target = "apr", expression = "java(dto.getExptrPrsnlList().get(3) != null ? dto.getExptrPrsnlList().get(3).getTotAmt() : 0)")
    // @Mapping(target = "may", expression = "java(dto.getExptrPrsnlList().get(4) != null ? dto.getExptrPrsnlList().get(4).getTotAmt() : 0)")
    // @Mapping(target = "jun", expression = "java(dto.getExptrPrsnlList().get(5) != null ? dto.getExptrPrsnlList().get(5).getTotAmt() : 0)")
    // @Mapping(target = "jul", expression = "java(dto.getExptrPrsnlList().get(6) != null ? dto.getExptrPrsnlList().get(6).getTotAmt() : 0)")
    // @Mapping(target = "aug", expression = "java(dto.getExptrPrsnlList().get(7) != null ? dto.getExptrPrsnlList().get(7).getTotAmt() : 0)")
    // @Mapping(target = "sep", expression = "java(dto.getExptrPrsnlList().get(8) != null ? dto.getExptrPrsnlList().get(8).getTotAmt() : 0)")
    // @Mapping(target = "oct", expression = "java(dto.getExptrPrsnlList().get(9) != null ? dto.getExptrPrsnlList().get(9).getTotAmt() : 0)")
    // @Mapping(target = "nov", expression = "java(dto.getExptrPrsnlList().get(10) != null ? dto.getExptrPrsnlList().get(10).getTotAmt() : 0)")
    // @Mapping(target = "dec", expression = "java(dto.getExptrPrsnlList().get(11) != null ? dto.getExptrPrsnlList().get(11).getTotAmt() : 0)")
    // ExptrPrsnlStatsListXlsxDto toListXlsxDto(final ExptrPrsnlStatsDto dto) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "itemList", expression = "java(dto.getItemEntityList())")
    ExptrPrsnlPaprEntity toEntity(final ExptrPrsnlPaprDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "itemList", expression = "java(dto.getItemEntityList())")
    void updateFromDto(
            final ExptrPrsnlPaprDto dto,
            final @MappingTarget ExptrPrsnlPaprEntity entity
    ) throws Exception;
}
