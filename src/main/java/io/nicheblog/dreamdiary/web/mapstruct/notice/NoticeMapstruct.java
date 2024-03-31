package io.nicheblog.dreamdiary.web.mapstruct.notice;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.CommentEmbedMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * NoticeMapstruct
 * <pre>
 *  공지사항 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CommentEmbedMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface NoticeMapstruct
        extends io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct<NoticeDto, NoticeListDto, NoticeEntity> {

    NoticeMapstruct INSTANCE = Mappers.getMapper(NoticeMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "comment", expression = "java(CommentEmbedMapstruct.INSTANCE.toDto(entity.comment))")       // 댓글 모듈
    // @Mapping(target = "viewerList", expression = "java(entity.getViewerDtoList())")
    // @Mapping(target = "managtrList", expression = "java(entity.getDtoList())")
    // @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DateUtils.PTN_DATETIME))")
    NoticeDto toDto(final NoticeEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "comment", expression = "java(CommentEmbedMapstruct.INSTANCE.toDto(entity.comment))")       // 댓글 모듈
    // @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DateUtils.PTN_DATETIME))")
    NoticeListDto toListDto(final NoticeEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    NoticeEntity toEntity(final NoticeDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final NoticeDto dto,
            final @MappingTarget NoticeEntity entity
    ) throws Exception;



}
