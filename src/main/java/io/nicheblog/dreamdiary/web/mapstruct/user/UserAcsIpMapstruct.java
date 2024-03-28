package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEntity;
import io.nicheblog.dreamdiary.web.model.user.UserAcsIpDto;
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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, })
public interface UserAcsIpMapstruct
        extends BaseMapstruct<UserAcsIpDto, UserAcsIpEntity> {

    UserAcsIpMapstruct INSTANCE = Mappers.getMapper(UserAcsIpMapstruct.class);

    /**
     * Dto -> Entity
     */
    @Override
    UserAcsIpEntity toEntity(final UserAcsIpDto dto) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    UserAcsIpDto toDto(final UserAcsIpEntity entity) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "userProfl", expression = "java(UserInfoMapstruct.INSTANCE.toEntity(dto.getUserProfl()))")
    void updateFromDto(
            final UserAcsIpDto dto,
            final @MappingTarget UserAcsIpEntity entity
    ) throws Exception;
}
