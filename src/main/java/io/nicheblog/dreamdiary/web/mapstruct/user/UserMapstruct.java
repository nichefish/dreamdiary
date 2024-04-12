package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserAuthRoleEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.mapstruct.user.emplym.UserEmplymMapstruct;
import io.nicheblog.dreamdiary.web.mapstruct.user.profl.UserProflMapstruct;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, UserStusEmbed.class, Collectors.class, UserProflMapstruct.class, UserEmplymMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface UserMapstruct
        extends BaseCrudMapstruct<UserDto.DTL, UserDto.LIST, UserEntity> {

    UserMapstruct INSTANCE = Mappers.getMapper(UserMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "password", expression = "java(null)")      // DTO로 패스워드 전달하지 않음
    @Mapping(target = "emailId", expression = "java(StringUtils.isNotEmpty(entity.getEmail()) ? entity.getEmail().substring(0, entity.getEmail().indexOf('@')) : \"\")")
    @Mapping(target = "emailDomain", expression = "java(StringUtils.isNotEmpty(entity.getEmail()) ? entity.getEmail().substring(entity.getEmail().indexOf('@')+1) : \"\")")
    @Mapping(target = "authStrList", expression = "java(entity.getAuthList().stream().map(UserAuthRoleEntity::getAuthCd).collect(Collectors.toList()))")      // 접속IP tagify 문자열 세팅
    @Mapping(target = "useAcsIp", expression = "java(\"Y\".equals(entity.getUseAcsIpYn()))")
    @Mapping(target = "acsIpListStr", expression = "java(String.join(\",\", entity.getAcsIpStrList()))")      // 접속IP tagify 문자열 세팅
    @Mapping(target = "isCf", expression = "java(entity.getAcntStus() != null && \"Y\".equals(entity.getAcntStus().getCfYn()))")
    @Mapping(target = "profl", expression = "java(UserProflMapstruct.INSTANCE.toDto(entity.getProfl()))")
    @Mapping(target = "emplym", expression = "java(UserEmplymMapstruct.INSTANCE.toDto(entity.getEmplym()))")
    UserDto.DTL toDto(final UserEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "isCf", expression = "java(entity.getAcntStus() != null && \"Y\".equals(entity.getAcntStus().getCfYn()))")
    @Mapping(target = "profl", expression = "java(UserProflMapstruct.INSTANCE.toDto(entity.getProfl()))")
    @Mapping(target = "emplym", expression = "java(UserEmplymMapstruct.INSTANCE.toDto(entity.getEmplym()))")
    UserDto.LIST toListDto(final UserEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "email", expression = "java(dto.getEmailId() + \"@\" + dto.getEmailDomain())")
    @Mapping(target = "acsIpList", expression = "java(dto.getAcsIpListStr())")
    @Mapping(target = "authList", expression = "java(dto.getAuthListStr())")
    @Mapping(target = "profl", expression = "java(UserProflMapstruct.INSTANCE.toEntity(dto.getProfl()))")
    @Mapping(target = "emplym", expression = "java(UserEmplymMapstruct.INSTANCE.toEntity(dto.getEmplym()))")
    UserEntity toEntity(final UserDto.DTL dto) throws Exception;

    @AfterMapping
    private void mapToEntityFields(final UserDto.DTL dto, final @MappingTarget UserEntity entity) throws Exception {
        // multiselect에서 날아온 권한 문자열 세팅
        String authListStr = dto.getAuthListStr();
        if (!StringUtils.isEmpty(authListStr)) {
            List<String> authStrList = List.of(authListStr.split(","));
            entity.setAuthList(authStrList.stream()
                    .map(UserAuthRoleEntity::new).collect(Collectors.toList()));
        }
    }

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "acsIpList", expression = "java(dto.getAcsIpListStr())")
    @Mapping(target = "authList", expression = "java(dto.getAuthListStr())")
    @Mapping(target = "profl", expression = "java(entity.getProflUpdt(dto.getProfl()))")
    @Mapping(target = "emplym", expression = "java(entity.getEmplymUpdt(dto.getEmplym()))")
    void updateFromDto(final UserDto.DTL dto, final @MappingTarget UserEntity entity) throws Exception;
}
