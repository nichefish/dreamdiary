package io.nicheblog.dreamdiary.web.mapstruct.user.emplym;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.crypto.CryptoUtils;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * UserEmplymMapstruct
 * <pre>
 *  사용자 인사정보 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {Constant.class, DateUtils.class, StringUtils.class, DatePtn.class, CryptoUtils.class})
public interface UserEmplymMapstruct
        extends BaseMapstruct<UserEmplymDto, UserEmplymEntity> {

    UserEmplymMapstruct INSTANCE = Mappers.getMapper(UserEmplymMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "ecnyDt", expression = "java(DateUtils.asStr(entity.getEcnyDt(), DatePtn.DATE))")
    @Mapping(target = "retireDt", expression = "java(DateUtils.asStr(entity.getRetireDt(), DatePtn.DATE))")
    @Mapping(target = "emplymEmailId", expression = "java(StringUtils.isNotEmpty(entity.getEmplymEmail()) ? entity.getEmplymEmail().substring(0, entity.getEmplymEmail().indexOf('@')) : \"\")")
    @Mapping(target = "emplymEmailDomain", expression = "java(StringUtils.isNotEmpty(entity.getEmplymEmail()) ? entity.getEmplymEmail().substring(entity.getEmplymEmail().indexOf('@')+1) : \"\")")
    @Mapping(target = "acntNo", expression = "java(StringUtils.isNotEmpty(entity.getAcntNo()) ? CryptoUtils.AES128.decrypt(entity.getAcntNo()) : null)")
    UserEmplymDto toDto(final UserEmplymEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "apntcYn", expression = "java(Constant.RANK_STAFF.equals(dto.getRankCd()) ? dto.getApntcYn() : null)")
    @Mapping(target = "ecnyDt", expression = "java(DateUtils.asDate(dto.getEcnyDt()))")
    @Mapping(target = "retireDt", expression = "java(\"Y\".equals(dto.getRetireYn()) ? DateUtils.asDate(dto.getRetireDt()) : null)")
    @Mapping(target = "emplymEmail", expression = "java(dto.getEmplymEmailId() + \"@\" + dto.getEmplymEmailDomain())")
    @Mapping(target = "acntNo", expression = "java(StringUtils.isNotEmpty(dto.getAcntNo()) ? CryptoUtils.AES128.encrypt(dto.getAcntNo()) : null)")
    UserEmplymEntity toEntity(final UserEmplymDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "apntcYn", expression = "java(\"STAFF\".equals(dto.getRankCd()) ? dto.getApntcYn() : null)")
    @Mapping(target = "ecnyDt", expression = "java(DateUtils.asDate(dto.getEcnyDt()))")
    @Mapping(target = "retireDt", expression = "java(DateUtils.asDate(dto.getRetireDt()))")
    @Mapping(target = "emplymEmail", expression = "java(dto.getEmplymEmailId() + \"@\" + dto.getEmplymEmailDomain())")
    @Mapping(target = "acntNo", expression = "java(StringUtils.isNotEmpty(dto.getAcntNo()) ?  CryptoUtils.AES128.encrypt(dto.getAcntNo()) : null)")
    void updateFromDto(final UserEmplymDto dto, final @MappingTarget UserEmplymEntity entity) throws Exception;
}