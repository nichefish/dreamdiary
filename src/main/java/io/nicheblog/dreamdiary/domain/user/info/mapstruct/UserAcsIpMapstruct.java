package io.nicheblog.dreamdiary.domain.user.info.mapstruct;

import io.nicheblog.dreamdiary.domain.user.info.entity.UserAcsIpEntity;
import io.nicheblog.dreamdiary.domain.user.info.model.UserAcsIpDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * UserAcsIpMapstruct
 * <pre>
 *  사용자 접속 IP MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, })
public interface UserAcsIpMapstruct
        extends BaseMapstruct<UserAcsIpDto, UserAcsIpEntity> {

    UserAcsIpMapstruct INSTANCE = Mappers.getMapper(UserAcsIpMapstruct.class);

    /**
     * Entity -> Dto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return Dto -- 변환된 Dto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    UserAcsIpDto toDto(final UserAcsIpEntity entity) throws Exception;

    /**
     * Dto -> Entity 변환
     *
     * @param dto 변환할 Dto 객체
     * @return Entity -- 변환된 Entity 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    UserAcsIpEntity toEntity(final UserAcsIpDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     *
     * @param dto 업데이트할 Dto 객체
     * @param entity 업데이트할 대상 Entity 객체
     * @throws Exception 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final UserAcsIpDto dto, final @MappingTarget UserAcsIpEntity entity) throws Exception;
}
