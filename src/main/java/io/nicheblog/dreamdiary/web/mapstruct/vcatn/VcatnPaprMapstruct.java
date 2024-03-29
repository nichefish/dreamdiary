package io.nicheblog.dreamdiary.web.mapstruct.vcatn;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseAuditListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.base.CommentEmbedMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.VcatnPaprEntity;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * VcatnPaprMapstruct
 * <pre>
 *  휴가계획서 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CommentEmbedMapstruct.class})
public interface VcatnPaprMapstruct
        extends BaseAuditListMapstruct<VcatnPaprDto, VcatnPaprListDto, VcatnPaprEntity> {

    VcatnPaprMapstruct INSTANCE = Mappers.getMapper(VcatnPaprMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Mapping(target = "vcatnSchdulList", expression = "java(entity.getVcatnSchdulDtoList())")
    @Mapping(target = "comment", expression = "java(CommentEmbedMapstruct.INSTANCE.toDto(entity.comment))")       // 댓글 모듈
    // @Mapping(target = "viewerList", expression = "java(entity.getViewerDtoList())")
    VcatnPaprDto toDto(final VcatnPaprEntity entity) throws Exception;

    /**
     * Entity -> listDto
     */
    @Mapping(target = "vcatnSchdulList", expression = "java(entity.getVcatnSchdulDtoList())")
    VcatnPaprListDto toListDto(final VcatnPaprEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Mapping(target = "vcatnSchdulList", expression = "java(dto.getVcatnSchdulEntityList())")
    @Mapping(target = "cfYn", expression = "java((dto.getCfYn() != null) ? dto.getCfYn() : \"N\")")
    VcatnPaprEntity toEntity(final VcatnPaprDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @Mapping(target = "vcatnSchdulList", expression = "java(dto.getVcatnSchdulEntityList())")
    @Mapping(target = "cfYn", expression = "java((dto.getCfYn() != null) ? dto.getCfYn() : \"N\")")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final VcatnPaprDto dto,
            final @MappingTarget VcatnPaprEntity entity
    ) throws Exception;
}
