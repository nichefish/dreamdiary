package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserAuthRoleEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.model.user.UserCttpcDto;
import io.nicheblog.dreamdiary.web.model.user.UserCttpcXlsxDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, UserStusEmbed.class, UserProflMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface UserMapstruct
        extends BaseCrudMapstruct<UserDto.DTL, UserDto.LIST, UserEntity> {

    UserMapstruct INSTANCE = Mappers.getMapper(UserMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "password", expression = "java(null)")      // DTO로 패스워드 전달하지 않음
    @Mapping(target = "useAcsIp", expression = "java(\"Y\".equals(entity.getUseAcsIpYn()))")
    @Mapping(target = "acsIpListStr", expression = "java(String.join(\",\", entity.getAcsIpStrList()))")      // 접속IP tagify 문자열 세팅
    @Mapping(target = "isCf", expression = "java(entity.getAcntStus() != null && \"Y\".equals(entity.getAcntStus().getCfYn()))")
    UserDto.DTL toDto(final UserEntity entity) throws Exception;

    @AfterMapping
    private void mapToDtoFields(final UserEntity entity, final @MappingTarget UserDto.DTL dto) throws Exception {
        // 권한 문자열 목록 세팅
        dto.setAuthStrList(entity.getAuthList().stream()
                .map(UserAuthRoleEntity::getAuthCd)
                .collect(Collectors.toList()));
    }

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "isCf", expression = "java(entity.getAcntStus() != null && \"Y\".equals(entity.getAcntStus().getCfYn()))")
    UserDto.LIST toListDto(final UserEntity entity) throws Exception;

    private void mapToListDtoFields(final UserEntity entity, final @MappingTarget UserDto.LIST dto) throws Exception {
        // 권한 문자열 목록 세팅
        dto.setAuthStrList(entity.getAuthList().stream()
                .map(UserAuthRoleEntity::getAuthCd)
                .collect(Collectors.toList()));
    }

    /**
     * Dto -> Entity
     */
    @Override
    UserEntity toEntity(final UserDto.DTL dto) throws Exception;

    @AfterMapping
    private void mapToEntityFields(final UserDto.DTL dto, final @MappingTarget UserEntity entity) throws Exception {
        // 권한 문자열 목록 세팅
        if (!"Y".equals(dto.getUseAcsIpYn())) {
            entity.getAcsIpList().clear();
        } else {
            String acsIpListStr = dto.getAcsIpListStr();
            if (StringUtils.isEmpty(acsIpListStr)) return;
            JSONArray jArray = new JSONArray(dto.getAcsIpListStr());
            List<UserAcsIpEntity> acsIpList = new ArrayList<>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = jArray.getJSONObject(i);
                UserAcsIpEntity acsIp = new UserAcsIpEntity();
                acsIp.setAcsIp(json.getString("value"));
                acsIpList.add(acsIp);
            }
            entity.setAcsIpList(acsIpList);
        }
    }

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
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final UserDto.DTL dto, final @MappingTarget UserEntity entity) throws Exception;
}
