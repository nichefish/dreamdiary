package io.nicheblog.dreamdiary.web.mapstruct.exptr.reqst;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseAuditListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.base.CommentEmbedMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.exptr.reqst.ExptrReqstEntity;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstDto;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ExptrReqstMapstruct
 * <pre>
 *  물품구매/경조사비 신청 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CommentEmbedMapstruct.class})
public interface ExptrReqstMapstruct
        extends BaseAuditListMapstruct<ExptrReqstDto, ExptrReqstListDto, ExptrReqstEntity> {

    ExptrReqstMapstruct INSTANCE = Mappers.getMapper(ExptrReqstMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "comment", expression = "java(CommentEmbedMapstruct.INSTANCE.toDto(entity.comment))")       // 댓글 모듈
    // @Mapping(target = "viewerList", expression = "java(entity.getViewerDtoList())")
    // @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DateUtils.PTN_DATETIME))")
    ExptrReqstDto toDto(final ExptrReqstEntity entity) throws Exception;

    /**
     * Entity -> listDto
     */
    @Mapping(target = "comment", expression = "java(CommentEmbedMapstruct.INSTANCE.toDto(entity.comment))")       // 댓글 모듈
    // @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DateUtils.PTN_DATETIME))")
    ExptrReqstListDto toListDto(final ExptrReqstEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    ExptrReqstEntity toEntity(final ExptrReqstDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final ExptrReqstDto dto,
            final @MappingTarget ExptrReqstEntity entity
    ) throws Exception;
}
