package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.AES128Utils;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserStusInfo;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * UserInfoMapstruct
 * <pre>
 *  사용자 정보 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, AES128Utils.class})
public interface UserProflMapstruct
        extends BaseMapstruct<UserProflDto, UserProflEntity> {

    UserProflMapstruct INSTANCE = Mappers.getMapper(UserProflMapstruct.class);

    /**
     * Dto -> Entity
     */
    @Override
    // @Mapping(target = "apntcYn", expression = "java(\"STAFF\".equals(dto.getJobTitleCd()) ? dto.getApntcYn() : null)")
    // @Mapping(target = "ecnyDt", expression = "java(DateUtils.asDate(dto.getEcnyDt()))")
    // @Mapping(target = "retireDt", expression = "java(\"Y\".equals(dto.getRetireYn()) ? DateUtils.asDate(dto.getRetireDt()) : null)")
    // @Mapping(target = "brthdy", expression = "java(DateUtils.asDate(dto.getBrthdy()))")
    // @Mapping(target = "email", expression = "java(dto.getEmailId() + \"@\" + dto.getEmailDomain())")
    // @Mapping(target = "acntNo", expression = "java(StringUtils.isNotEmpty(dto.getAcntNo()) ? AES128Utils.encrypt(dto.getAcntNo()) : null)")
    UserProflEntity toEntity(final UserProflDto dto) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    // @Mapping(target = "cmpyNm", expression = "java((entity.getCmpyCdInfo() != null) ? entity.getCmpyCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "teamNm", expression = "java((entity.getTeamCdInfo() != null) ? entity.getTeamCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "emplymNm", expression = "java((entity.getEmplymCdInfo() != null) ? entity.getEmplymCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "jobTitleNm", expression = "java((entity.getJobTitleCdInfo() != null) ? entity.getJobTitleCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "ecnyDt", expression = "java(DateUtils.asStr(entity.getEcnyDt(), DateUtils.PTN_DATE))")
    // @Mapping(target = "retireDt", expression = "java(DateUtils.asStr(entity.getRetireDt(), DateUtils.PTN_DATE))")
    // @Mapping(target = "brthdy", expression = "java(DateUtils.asStr(entity.getBrthdy(), DateUtils.PTN_DATE))")
    // @Mapping(target = "emailId", expression = "java(StringUtils.isNotEmpty(entity.getEmail()) ? entity.getEmail().substring(0, entity.getEmail().indexOf('@')) : null)")
    // @Mapping(target = "emailDomain", expression = "java(StringUtils.isNotEmpty(entity.getEmail()) ? entity.getEmail().substring(entity.getEmail().indexOf('@')+1) : null)")
    // @Mapping(target = "acntNo", expression = "java(StringUtils.isNotEmpty(entity.getAcntNo()) ? AES128Utils.decrypt(entity.getAcntNo()) : null)")
    UserProflDto toDto(final UserProflEntity entity) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "apntcYn", expression = "java(\"STAFF\".equals(dto.getJobTitleCd()) ? dto.getApntcYn() : null)")
    // @Mapping(target = "ecnyDt", expression = "java(DateUtils.asDate(dto.getEcnyDt()))")
    // @Mapping(target = "retireDt", expression = "java(DateUtils.asDate(dto.getRetireDt()))")
    // @Mapping(target = "brthdy", expression = "java(StringUtils.isNotEmpty(dto.getBrthdy()) ? DateUtils.asDate(dto.getBrthdy()) : null)")
    // @Mapping(target = "email", expression = "java(dto.getEmailId() + \"@\" + dto.getEmailDomain())")
    // @Mapping(target = "acntNo", expression = "java(StringUtils.isNotEmpty(dto.getAcntNo()) ? AES128Utils.encrypt(dto.getAcntNo()) : null)")
    void updateFromDto(
            final UserProflDto dto,
            final @MappingTarget UserProflEntity entity
    ) throws Exception;
}