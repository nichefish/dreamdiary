package io.nicheblog.dreamdiary.web.mapstruct.user.emplym;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class})
public interface UserEmplymMapstruct
        extends BaseMapstruct<UserProflDto, UserProflEntity> {

    UserEmplymMapstruct INSTANCE = Mappers.getMapper(UserEmplymMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    // @Mapping(target = "ecnyDt", expression = "java(DateUtils.asStr(entity.getEcnyDt(), DatePtn.DATE))")
    // @Mapping(target = "retireDt", expression = "java(DateUtils.asStr(entity.getRetireDt(), DatePtn.DATE))")
    // @Mapping(target = "emailId", expression = "java(StringUtils.isNotEmpty(entity.getEmail()) ? entity.getEmail().substring(0, entity.getEmail().indexOf('@')) : null)")
    // @Mapping(target = "emailDomain", expression = "java(StringUtils.isNotEmpty(entity.getEmail()) ? entity.getEmail().substring(entity.getEmail().indexOf('@')+1) : null)")
    // @Mapping(target = "acntNo", expression = "java(StringUtils.isNotEmpty(entity.getAcntNo()) ? CryptoUtils.AES128.decrypt(entity.getAcntNo()) : null)")
    UserProflDto toDto(final UserProflEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    // @Mapping(target = "apntcYn", expression = "java(\"STAFF\".equals(dto.getRankCd()) ? dto.getApntcYn() : null)")
    // @Mapping(target = "ecnyDt", expression = "java(DateUtils.asDate(dto.getEcnyDt()))")
    // @Mapping(target = "retireDt", expression = "java(\"Y\".equals(dto.getRetireYn()) ? DateUtils.asDate(dto.getRetireDt()) : null)")
    // @Mapping(target = "email", expression = "java(dto.getEmailId() + \"@\" + dto.getEmailDomain())")
    // @Mapping(target = "acntNo", expression = "java(StringUtils.isNotEmpty(dto.getAcntNo()) ? CryptoUtils.AES128.encrypt(dto.getAcntNo()) : null)")
    UserProflEntity toEntity(final UserProflDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "apntcYn", expression = "java(\"STAFF\".equals(dto.getRankCd()) ? dto.getApntcYn() : null)")
    // @Mapping(target = "ecnyDt", expression = "java(DateUtils.asDate(dto.getEcnyDt()))")
    // @Mapping(target = "retireDt", expression = "java(DateUtils.asDate(dto.getRetireDt()))")
    // @Mapping(target = "email", expression = "java(dto.getEmailId() + \"@\" + dto.getEmailDomain())")
    // @Mapping(target = "acntNo", expression = "java(StringUtils.isNotEmpty(dto.getAcntNo()) ?  CryptoUtils.AES128.encrypt(dto.getAcntNo()) : null)")
    void updateFromDto(final UserProflDto dto, final @MappingTarget UserProflEntity entity) throws Exception;
}