package io.nicheblog.dreamdiary.web.mapstruct.user.profl;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * UserProflMapstruct
 * <pre>
 *  사용자 프로필 정보 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class})
public interface UserProflMapstruct
        extends BaseMapstruct<UserProflDto, UserProflEntity> {

    UserProflMapstruct INSTANCE = Mappers.getMapper(UserProflMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "brthdy", expression = "java(DateUtils.asStr(entity.getBrthdy(), DatePtn.DATE))")
    UserProflDto toDto(final UserProflEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "brthdy", expression = "java(DateUtils.asDate(dto.getBrthdy()))")
    UserProflEntity toEntity(final UserProflDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "brthdy", expression = "java(DateUtils.asDate(dto.getBrthdy()))")
    void updateFromDto(final UserProflDto dto, final @MappingTarget UserProflEntity entity) throws Exception;
}