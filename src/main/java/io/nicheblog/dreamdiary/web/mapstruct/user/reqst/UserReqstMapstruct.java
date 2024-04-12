package io.nicheblog.dreamdiary.web.mapstruct.user.reqst;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.mapstruct.user.emplym.UserEmplymMapstruct;
import io.nicheblog.dreamdiary.web.mapstruct.user.profl.UserProflMapstruct;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * UserReqstMapstruct
 * <pre>
 *  사용자 신규계정 신청 정보 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, UserStusEmbed.class, UserProflMapstruct.class, UserEmplymMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface UserReqstMapstruct
        extends BaseMapstruct<UserReqstDto, UserEntity> {

    UserReqstMapstruct INSTANCE = Mappers.getMapper(UserReqstMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "password", expression = "java(null)")      // DTO로 패스워드 전달하지 않음
    @Mapping(target = "emailId", expression = "java(StringUtils.isNotEmpty(entity.getEmail()) ? entity.getEmail().substring(0, entity.getEmail().indexOf('@')) : \"\")")
    @Mapping(target = "emailDomain", expression = "java(StringUtils.isNotEmpty(entity.getEmail()) ? entity.getEmail().substring(entity.getEmail().indexOf('@')+1) : \"\")")
    UserReqstDto toDto(final UserEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Mapping(target = "email", expression = "java(dto.getEmailId() + \"@\" + dto.getEmailDomain())")
    @Mapping(target = "acsIpList", expression = "java(dto.getAcsIpListStr())")
    @Mapping(target = "profl", expression = "java(UserProflMapstruct.INSTANCE.toEntity(dto.getProfl()))")
    @Mapping(target = "emplym", expression = "java(UserEmplymMapstruct.INSTANCE.toEntity(dto.getEmplym()))")
    UserEntity toEntity(final UserReqstDto dto) throws Exception;
}
