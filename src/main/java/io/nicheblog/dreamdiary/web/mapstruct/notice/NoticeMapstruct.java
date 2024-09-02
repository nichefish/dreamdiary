package io.nicheblog.dreamdiary.web.mapstruct.notice;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeXlsxDto;
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
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CmmUtils.class}, builder = @Builder(disableBuilder = true))
public interface NoticeMapstruct
        extends BasePostMapstruct<NoticeDto.DTL, NoticeDto.LIST, NoticeEntity> {

    NoticeMapstruct INSTANCE = Mappers.getMapper(NoticeMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "markdownCn", expression = "java(CmmUtils.markdown(entity.getCn()))")
    NoticeDto.DTL toDto(final NoticeEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "markdownCn", expression = "java(CmmUtils.markdown(entity.getCn()))")
    NoticeDto.LIST toListDto(final NoticeEntity entity) throws Exception;

    /**
     * Entity -> XlsxDto
     */
    @Named("toXlsxDto")
    NoticeXlsxDto toXlsxDto(final NoticeEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    NoticeEntity toEntity(final NoticeDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final NoticeDto.DTL dto, final @MappingTarget NoticeEntity entity) throws Exception;
}
