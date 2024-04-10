package io.nicheblog.dreamdiary.web.mapstruct.user.reqst;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.entity.user.reqst.UserReqstEntity;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserProflMapstruct;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * UserMapstruct
 * <pre>
 *  사용자(계정) 정보 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, UserStusEmbed.class, UserProflMapstruct.class}, builder = @Builder(disableBuilder = true))
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
    UserReqstEntity toEntity(final UserReqstDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final UserReqstDto dto, final @MappingTarget UserReqstEntity entity) throws Exception;

    @AfterMapping
    private void mapToEntityFields(final UserReqstDto dto, final @MappingTarget UserEntity entity) throws Exception {
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

}
