package io.nicheblog.dreamdiary.global.cmm.cd.mapstruct;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCd;
import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCd;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * CdMapstruct
 * <pre>
 *  코드 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface CdMapstruct {

    CdMapstruct INSTANCE = Mappers.getMapper(CdMapstruct.class);

    /**
     * Entity -> Dto
     */
    ClCd toDto(final ClCdEntity entity) throws Exception;

    DtlCd toDto(final DtlCdEntity entity) throws Exception;
}
