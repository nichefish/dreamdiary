package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseAuditListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEmbed;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.model.user.UserCttpcListDto;
import io.nicheblog.dreamdiary.web.model.user.UserCttpcListXlsxDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.UserListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * UserMapstruct
 * <pre>
 *  사용자(계정) 정보 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, UserStusEmbed.class, UserAcsIpEmbed.class, UserProflMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface UserMapstruct
        extends BaseAuditListMapstruct<UserDto, UserListDto, UserEntity> {

    UserMapstruct INSTANCE = Mappers.getMapper(UserMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toDto(entity.getUserProfl()))")
    // @Mapping(target = "password", expression = "java(null)")      // DTO로 패스워드 전달하지 않음
    // @Mapping(target = "authNm", expression = "java(entity.getAuthCdInfo() != null ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    UserDto toDto(final UserEntity entity) throws Exception;

    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toDto(entity.getUserProfl()))")
    // @Mapping(target = "password", expression = "java(null)")      // DTO로 패스워드 전달하지 않음
    // @Mapping(target = "authNm", expression = "java(entity.getAuthCdInfo() != null ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    // UserDto toDto(final UserReqstEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    // @Mapping(target = "authNm", expression = "java((entity.getAuthCdInfo() != null) ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "userNm", expression = "java(entity.getUserProfl() != null ? entity.getUserProfl().getUserNm() : entity.getNickNm())")
    // @Mapping(target = "jobTitleNm", expression = "java((entity.getUserProfl() != null && StringUtils.isNotEmpty(entity.getUserProfl().getJobTitleCd())) ? entity.getUserProfl().getJobTitleCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "apntcYn", expression = "java(entity.getUserProfl() != null ? entity.getUserProfl().getApntcYn() : null)")
    // @Mapping(target = "cmpyNm", expression = "java((entity.getUserProfl() != null && StringUtils.isNotEmpty(entity.getUserProfl().getCmpyCd())) ? entity.getUserProfl().getCmpyCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "teamNm", expression = "java((entity.getUserProfl() != null && StringUtils.isNotEmpty(entity.getUserProfl().getTeamCd())) ? entity.getUserProfl().getTeamCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "emplymNm", expression = "java((entity.getUserProfl() != null && StringUtils.isNotEmpty(entity.getUserProfl().getEmplymCd())) ? entity.getUserProfl().getEmplymCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "ecnyDt", expression = "java(entity.getUserProfl() != null ? DateUtils.asStr(entity.getUserProfl().getEcnyDt(), DateUtils.PTN_DATE) : null)")
    // @Mapping(target = "cttpc", expression = "java(entity.getUserProfl() != null ? entity.getUserProfl().getCttpc() : null)")
    // @Mapping(target = "email", expression = "java(entity.getUserProfl() != null ? entity.getUserProfl().getEmail() : null)")
    // @Mapping(target = "brthdy", expression = "java(entity.getUserProfl() != null ? DateUtils.asStr(entity.getUserProfl().getBrthdy(), DateUtils.PTN_DATE) : null)")
    // @Mapping(target = "lunarYn", expression = "java(entity.getUserProfl() != null ? entity.getUserProfl().getLunarYn() : null)")
    // @Mapping(target = "userProflYn", expression = "java(entity.getUserProfl() != null ? \"Y\" : \"N\")")
    // @Mapping(target = "retireYn", expression = "java(entity.getUserProfl() != null ? entity.getUserProfl().getRetireYn() : null)")
    UserListDto toListDto(final UserEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserProfl()))")
    @Mapping(target = "acsIpInfo", expression = "java(new UserAcsIpInfo(dto))")
    @Mapping(target = "acntStus", expression = "java(new UserStusInfo(dto))")
    UserEntity toEntity(final UserDto dto) throws Exception;

    /**
     * Entity -> ListXlsxDto
     */
    // @Mapping(target = "authNm", expression = "java((entity.getAuthCdInfo() != null) ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "lstLgnDt", expression = "java(DateUtils.asStr(entity.getLstLgnDt(), DateUtils.PTN_DATETIME))")
    // UserListXlsxDto toListXlsxDto(final UserEntity entity) throws Exception;

    /**
     * dtlDtoToListDto
     */
    // @Mapping(target = "ecnyDt", expression = "java(user.getUserProfl() != null ? DateUtils.asStr(user.getUserProfl().getEcnyDt(), DateUtils.PTN_DATE) : null)")
    UserListDto dtlDtoToListDto(final UserDto user) throws Exception;

    /**
     * toCttpcListDto
     */
    @Mapping(target = "jobTitleNm", expression = "java(\"사원\".equals(user.getJobTitleNm()) ? \"Y\".equals(user.getApntcYn()) ? \"수습사원\" : user.getJobTitleNm() : user.getJobTitleNm())")
    UserCttpcListDto toCttpcListDto(final UserListDto user);

    /**
     * toCttpcListXlsxDto
     */
    @Mapping(target = "jobTitleNm", expression = "java(\"사원\".equals(user.getJobTitleNm()) ? \"Y\".equals(user.getApntcYn()) ? \"수습사원\" : user.getJobTitleNm() : user.getJobTitleNm())")
    UserCttpcListXlsxDto toCttpcListXlsxDto(final UserListDto user);

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserProfl()))")
    void updateFromDto(
            final UserDto dto,
            final @MappingTarget UserEntity entity
    ) throws Exception;


}
