package io.nicheblog.dreamdiary.global.cmm.cd.mapstruct;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCdDto;
import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCdDto;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * CdMapstruct
 * <pre>
 *  코드 MapStruct 기반 Mapper 인터페이스
 *  (단순 코드 조회용이므로 auditor 정보 매핑 상속받지 않음)
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface CdMapstruct {

    CdMapstruct INSTANCE = Mappers.getMapper(CdMapstruct.class);

    /**
     * Entity -> Dto (분류코드)
     */
    ClCdDto toDto(final ClCdEntity entity) throws Exception;

    /**
     * Entity -> Dto (상세코드)
     */
    DtlCdDto toDto(final DtlCdEntity entity) throws Exception;
}
