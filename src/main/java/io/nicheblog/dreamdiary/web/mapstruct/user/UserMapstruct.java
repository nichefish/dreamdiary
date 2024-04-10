package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.MapstructHelper;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEmbed;
import io.nicheblog.dreamdiary.web.entity.user.UserAuthRoleEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.model.user.UserAcsIpCmpstn;
import io.nicheblog.dreamdiary.web.model.user.UserCttpcDto;
import io.nicheblog.dreamdiary.web.model.user.UserCttpcXlsxDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import javax.persistence.PostLoad;
import java.util.stream.Collectors;

/**
 * UserMapstruct
 * <pre>
 *  사용자(계정) 정보 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, UserStusEmbed.class, UserAcsIpEmbed.class, UserAcsIpCmpstn.class, UserProflMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface UserMapstruct
        extends BaseCrudMapstruct<UserDto.DTL, UserDto.LIST, UserEntity> {

    UserMapstruct INSTANCE = Mappers.getMapper(UserMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "password", expression = "java(null)")      // DTO로 패스워드 전달하지 않음
    @Mapping(target = "isCf", expression = "java(entity.getAcntStus() != null && \"Y\".equals(entity.getAcntStus().getCfYn()))")
    @Mapping(target = "acsIpInfo", expression = "java(new UserAcsIpCmpstn(entity.getAcsIpInfo()))")
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toDto(entity.getUserProfl()))")
    UserDto.DTL toDto(final UserEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "isCf", expression = "java(entity.getAcntStus() != null && \"Y\".equals(entity.getAcntStus().getCfYn()))")
    // @Mapping(target = "authNm", expression = "java((entity.getAuthCdInfo() != null) ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "userNm", expression = "java(entity.getUserProfl() != null ? entity.getUserProfl().getUserNm() : entity.getNickNm())")
    // @Mapping(target = "jobTitleNm", expression = "java((entity.getUserProfl() != null && StringUtils.isNotEmpty(entity.getUserProfl().getJobTitleCd())) ? entity.getUserProfl().getJobTitleCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "apntcYn", expression = "java(entity.getUserProfl() != null ? entity.getUserProfl().getApntcYn() : null)")
    // @Mapping(target = "cmpyNm", expression = "java((entity.getUserProfl() != null && StringUtils.isNotEmpty(entity.getUserProfl().getCmpyCd())) ? entity.getUserProfl().getCmpyCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "teamNm", expression = "java((entity.getUserProfl() != null && StringUtils.isNotEmpty(entity.getUserProfl().getTeamCd())) ? entity.getUserProfl().getTeamCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "emplymNm", expression = "java((entity.getUserProfl() != null && StringUtils.isNotEmpty(entity.getUserProfl().getEmplymCd())) ? entity.getUserProfl().getEmplymCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "ecnyDt", expression = "java(entity.getUserProfl() != null ? DateUtils.asStr(entity.getUserProfl().getEcnyDt(), DatePtn.DATE) : null)")
    // @Mapping(target = "userProflYn", expression = "java(entity.getUserProfl() != null ? \"Y\" : \"N\")")
    // @Mapping(target = "retireYn", expression = "java(entity.getUserProfl() != null ? entity.getUserProfl().getRetireYn() : null)")
    UserDto.LIST toListDto(final UserEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserProfl()))")
    @Mapping(target = "acsIpInfo", expression = "java(new UserAcsIpEmbed(dto.getAcsIpInfo()))")
    UserEntity toEntity(final UserDto.DTL dto) throws Exception;

    /**
     * Entity -> ListXlsxDto
     */
    // @Mapping(target = "authNm", expression = "java((entity.getAuthCdInfo() != null) ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "lstLgnDt", expression = "java(DateUtils.asStr(entity.getLstLgnDt(), DatePtn.DATETIME))")
    // UserListXlsxDto toListXlsxDto(final UserEntity entity) throws Exception;

    /**
     * dtlDtoToListDto
     */
    // @Mapping(target = "ecnyDt", expression = "java(user.getUserProfl() != null ? DateUtils.asStr(user.getUserProfl().getEcnyDt(), DatePtn.DATE) : null)")
    UserDto.LIST dtlDtoToListDto(final UserDto user) throws Exception;

    /**
     * toCttpcListDto
     */
    // @Mapping(target = "jobTitleNm", expression = "java(\"사원\".equals(user.getJobTitleNm()) ? \"Y\".equals(user.getApntcYn()) ? \"수습사원\" : user.getJobTitleNm() : user.getJobTitleNm())")
    UserCttpcDto toCttpcDto(final UserDto.LIST user);

    /**
     * toCttpcListXlsxDto
     */
    // @Mapping(target = "jobTitleNm", expression = "java(\"사원\".equals(user.getJobTitleNm()) ? \"Y\".equals(user.getApntcYn()) ? \"수습사원\" : user.getJobTitleNm() : user.getJobTitleNm())")
    UserCttpcXlsxDto toCttpcListXlsxDto(final UserDto.LIST user);

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @Mapping(target = "acsIpInfo", expression = "java(new UserAcsIpEmbed(dto.getAcsIpInfo()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserProfl()))")
    void updateFromDto(final UserDto.DTL dto, final @MappingTarget UserEntity entity) throws Exception;

    /**
     * Entity -> Dto
     */
    @AfterMapping
    default void mapBaseListFields(final UserEntity entity, final @MappingTarget UserDto dto) throws Exception {
        // 권한 문자열 목록 세팅
        dto.setAuthStrList(entity.getAuthList().stream()
                .map(UserAuthRoleEntity::getAuthCd)
                .collect(Collectors.toList()));
    }


}
