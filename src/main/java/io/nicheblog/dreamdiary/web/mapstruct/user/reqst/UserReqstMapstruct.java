package io.nicheblog.dreamdiary.web.mapstruct.user.reqst;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEmbed;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.entity.user.reqst.UserReqstEntity;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserProflMapstruct;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
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
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, UserStusEmbed.class, UserAcsIpEmbed.class, UserProflMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface UserReqstMapstruct
        extends BaseMapstruct<UserReqstDto, UserReqstEntity> {

    UserReqstMapstruct INSTANCE = Mappers.getMapper(UserReqstMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    UserReqstDto toDto(final UserReqstEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Mapping(target = "acsIpInfo", expression = "java(new UserAcsIpEmbed(dto.getAcsIpInfo()))")
    UserReqstEntity toEntity(final UserReqstDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final UserReqstDto dto, final @MappingTarget UserReqstEntity entity) throws Exception;
}
