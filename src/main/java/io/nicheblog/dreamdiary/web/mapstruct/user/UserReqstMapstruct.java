package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEmbed;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.entity.user.reqst.UserReqstEntity;
import io.nicheblog.dreamdiary.web.model.user.UserReqstDto;
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
public interface UserReqstMapstruct
        extends BaseMapstruct<UserReqstDto, UserReqstEntity> {

    UserReqstMapstruct INSTANCE = Mappers.getMapper(UserReqstMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toDto(entity.getUserProfl()))")
    @Mapping(target = "password", expression = "java(null)")      // DTO로 패스워드 전달하지 않음
    // @Mapping(target = "authNm", expression = "java(entity.getAuthCdInfo() != null ? entity.getAuthCdInfo().getDtlCdNm() : null)")
    UserReqstDto toDto(final UserReqstEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserProfl()))")
    UserReqstEntity toEntity(final UserReqstDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserProfl()))")
    void updateFromDto(
            final UserReqstDto dto,
            final @MappingTarget UserReqstEntity entity
    ) throws Exception;
}
