package io.nicheblog.dreamdiary.global.auth.mapstruct;

import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * AuthRoleMapstruct
 * <pre>
 *  분류코드 관리 MapStruct 기반 Mapper 인터페이스
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface AuthInfoMapstruct
        extends BaseMapstruct<AuthInfo, UserEntity> {

    AuthInfoMapstruct INSTANCE = Mappers.getMapper(AuthInfoMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    // @Mapping(target = "authList", expression = "java(entity.getDtlCdDtoList())")
    @Mapping(target = "acsIpList", expression = "java(entity.acsIpInfo.getAcsIpStrList())")
    @Mapping(target = "lockedYn", expression = "java(entity.acntStus.getLockedYn())")
    @Mapping(target = "lstLgnDt", expression = "java(entity.acntStus.getLstLgnDt() != null ? entity.acntStus.getLstLgnDt() : entity.getRegDt())")       // 최종접속일 또는 등록일
    @Mapping(target = "pwChgDt", expression = "java(entity.acntStus.getPwChgDt() != null ? entity.acntStus.getPwChgDt() : entity.getRegDt())")          // 최종비밀번호변경일 또는 등록일
    @Mapping(target = "needsPwReset", expression = "java(entity.acntStus.getNeedsPwReset())")
    @Mapping(target = "cfYn", expression = "java(entity.acntStus.getCfYn())")
    AuthInfo toDto(final UserEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    UserEntity toEntity(final AuthInfo dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final AuthInfo dto,
            final @MappingTarget UserEntity entity
    ) throws Exception;
}
