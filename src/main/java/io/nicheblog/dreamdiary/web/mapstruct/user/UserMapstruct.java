package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, UserInfoMapstruct.class})
public interface UserMapstruct
        extends BaseListMapstruct<UserDto, UserListDto, UserEntity> {

    UserMapstruct INSTANCE = Mappers.getMapper(UserMapstruct.class);

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "userInfo", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserInfo()))")
    UserEntity toEntity(final UserDto dto) throws Exception;

    /**
     * Dto -> Entity
     */
    // @Mapping(target = "userInfo", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserInfo()))")
    // UserReqstEntity toReqstEntity(final UserDto dto) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    // @Mapping(target = "userInfo", expression = "java(UserInfoMapstruct.INSTANCE.toDto(entity.getUserInfo()))")
    // @Mapping(target = "userPw", expression = "java(null)")      // DTO로 패스워드 전달하지 않음
    // @Mapping(target = "authNm", expression = "java(entity.getAuthCdInfo() != null ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    UserDto toDto(final UserEntity entity) throws Exception;

    // @Mapping(target = "userInfo", expression = "java(UserInfoMapstruct.INSTANCE.toDto(entity.getUserInfo()))")
    // @Mapping(target = "userPw", expression = "java(null)")      // DTO로 패스워드 전달하지 않음
    // @Mapping(target = "authNm", expression = "java(entity.getAuthCdInfo() != null ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    // UserDto toDto(final UserReqstEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    // @Mapping(target = "authNm", expression = "java((entity.getAuthCdInfo() != null) ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "regstrNm", expression = "java((entity.getRegstrInfo() != null) ? entity.getRegstrInfo().getNickNm() : null)")
    // @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    // @Mapping(target = "userNm", expression = "java(entity.getUserInfo() != null ? entity.getUserInfo().getUserNm() : entity.getNickNm())")
    // @Mapping(target = "jobTitleNm", expression = "java((entity.getUserInfo() != null && StringUtils.isNotEmpty(entity.getUserInfo().getJobTitleCd())) ? entity.getUserInfo().getJobTitleCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "apntcYn", expression = "java(entity.getUserInfo() != null ? entity.getUserInfo().getApntcYn() : null)")
    // @Mapping(target = "cmpyNm", expression = "java((entity.getUserInfo() != null && StringUtils.isNotEmpty(entity.getUserInfo().getCmpyCd())) ? entity.getUserInfo().getCmpyCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "teamNm", expression = "java((entity.getUserInfo() != null && StringUtils.isNotEmpty(entity.getUserInfo().getTeamCd())) ? entity.getUserInfo().getTeamCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "emplymNm", expression = "java((entity.getUserInfo() != null && StringUtils.isNotEmpty(entity.getUserInfo().getEmplymCd())) ? entity.getUserInfo().getEmplymCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "ecnyDt", expression = "java(entity.getUserInfo() != null ? DateUtils.asStr(entity.getUserInfo().getEcnyDt(), DateUtils.PTN_DATE) : null)")
    // @Mapping(target = "cttpc", expression = "java(entity.getUserInfo() != null ? entity.getUserInfo().getCttpc() : null)")
    // @Mapping(target = "email", expression = "java(entity.getUserInfo() != null ? entity.getUserInfo().getEmail() : null)")
    // @Mapping(target = "brthdy", expression = "java(entity.getUserInfo() != null ? DateUtils.asStr(entity.getUserInfo().getBrthdy(), DateUtils.PTN_DATE) : null)")
    // @Mapping(target = "lunarYn", expression = "java(entity.getUserInfo() != null ? entity.getUserInfo().getLunarYn() : null)")
    // @Mapping(target = "userInfoYn", expression = "java(entity.getUserInfo() != null ? \"Y\" : \"N\")")
    // @Mapping(target = "retireYn", expression = "java(entity.getUserInfo() != null ? entity.getUserInfo().getRetireYn() : null)")
    UserListDto toListDto(final UserEntity entity) throws Exception;

    /**
     * Entity -> ListXlsxDto
     */
    // @Mapping(target = "authNm", expression = "java((entity.getAuthCdInfo() != null) ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "lstLgnDt", expression = "java(DateUtils.asStr(entity.getLstLgnDt(), DateUtils.PTN_DATETIME))")
    // UserListXlsxDto toListXlsxDto(final UserEntity entity) throws Exception;

    /**
     * dtlDtoToListDto
     */
    // @Mapping(target = "ecnyDt", expression = "java(user.getUserInfo() != null ? DateUtils.asStr(user.getUserInfo().getEcnyDt(), DateUtils.PTN_DATE) : null)")
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
    @Mapping(target = "userInfo", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserInfo()))")
    void updateFromDto(
            final UserDto dto,
            final @MappingTarget UserEntity entity
    ) throws Exception;


}
