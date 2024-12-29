package io.nicheblog.dreamdiary.domain.notice.mapstruct;

import io.nicheblog.dreamdiary.domain.notice.entity.NoticeEntity;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeDto;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeXlsxDto;
import io.nicheblog.dreamdiary.global._common.cd.utils.CdUtils;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * NoticeMapstruct
 * <pre>
 *  공지사항 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CmmUtils.class, CdUtils.class}, builder = @Builder(disableBuilder = true))
public interface NoticeMapstruct
        extends BasePostMapstruct<NoticeDto.DTL, NoticeDto.LIST, NoticeEntity> {

    NoticeMapstruct INSTANCE = Mappers.getMapper(NoticeMapstruct.class);

    /**
     * Entity -> Dto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return Dto -- 변환된 Dto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toDto")
    @Mapping(target = "ctgrNm", expression = "java(CdUtils.getDtlCdNm(\"NOTICE_CTGR_CD\", entity.getCtgrCd()))")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    NoticeDto.DTL toDto(final NoticeEntity entity) throws Exception;

    /**
     * Entity -> ListDto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return ListDto -- 변환된 ListDto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "ctgrNm", expression = "java(CdUtils.getDtlCdNm(\"NOTICE_CTGR_CD\", entity.getCtgrCd()))")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    NoticeDto.LIST toListDto(final NoticeEntity entity) throws Exception;

    /**
     * Entity -> XlsxDto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return XlsxDto - 변환된 XlsxDto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Named("toXlsxDto")
    NoticeXlsxDto toXlsxDto(final NoticeEntity entity) throws Exception;

    /**
     * Dto -> Entity 변환
     *
     * @param dto 변환할 Dto 객체
     * @return Entity -- 변환된 Entity 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    NoticeEntity toEntity(final NoticeDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     *
     * @param dto 업데이트할 DTO 객체
     * @param entity 업데이트할 대상 엔티티 객체
     * @throws Exception 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final NoticeDto.DTL dto, final @MappingTarget NoticeEntity entity) throws Exception;
}
