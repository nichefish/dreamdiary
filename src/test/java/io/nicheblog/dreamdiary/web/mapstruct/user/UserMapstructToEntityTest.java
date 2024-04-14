package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.UserDtoTestFactory;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDtoTestFactory;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDtoTestFactory;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMapstructToEntityTest
 * <pre>
 *  사용자 계정 신청 Mapstruct 매핑 테스트 모듈 :: toEntity 분리
 * </pre>
 *
 * @author nichefish
 */
@Log4j2
class UserMapstructToEntityTest {

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        UserDto.DTL userDto = UserDtoTestFactory.createUser();

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        // 권한 관련 매핑 검증
        assertFalse(CollectionUtils.isEmpty(entity.getAuthList()));
        assertEquals(entity.getAuthList().size(), 2);
        assertEquals(entity.getAuthList().get(0).getAuthCd(), Constant.AUTH_USER);
        assertEquals(entity.getAuthList().get(1).getAuthCd(), Constant.AUTH_MNGR);
        // 이메일 변환 로직
        assertEquals(entity.getEmail(), userDto.getEmailId() + "@" + userDto.getEmailDomain());
        // 접속 IP 관련
        assertEquals(entity.getUseAcsIpYn(), userDto.getUseAcsIpYn());
        assertEquals(entity.getAcsIpList().get(0).getAcsIp(), "1.1.1.1");
        assertEquals(entity.getAcsIpList().get(1).getAcsIp(), "2.2.2.2");
    }

    @Test
    void toEntity_checkProfl() throws Exception {
        // Given::
        UserProflDto userProflDto = UserProflDtoTestFactory.createUserProfl();
        UserDto.DTL userDto = UserDtoTestFactory.createUser(userProflDto);

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        UserProflEntity userProflEntity = entity.getProfl();
        assertNotNull(userProflEntity);
        // 날짜 변환 체크
        assertEquals(userProflEntity.getBrthdy(), DateUtils.asDate("2000-01-01"));
    }

    @Test
    void toEntity_checkEmplym() throws Exception {
        // Given::
        UserEmplymDto userEmplymDto = UserEmplymDtoTestFactory.createUserEmplym();
        UserDto.DTL userDto = UserDtoTestFactory.createUser(userEmplymDto);

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        UserEmplymEntity userEmplymEntity = entity.getEmplym();
        assertNotNull(userEmplymEntity);
        // 날짜 변환 체크
        assertEquals(userEmplymEntity.getEcnyDt(), DateUtils.asDate("2000-01-01"));
        assertEquals(userEmplymEntity.getRetireDt(), DateUtils.asDate("2000-01-01"));
        // 이메일 변환 로직
        assertEquals(userEmplymEntity.getEmplymEmail(), userEmplymDto.getEmplymEmailId() + "@" + userEmplymDto.getEmplymEmailDomain());
    }

}