package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.test.user.UserTestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UserMapstructTest
 * <pre>
 *  사용자 계정 신청 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
class UserMapstructTest {

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        UserDto.DTL userDto = UserTestUtils.createUser(null, null);

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
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
        UserProflDto userProflDto = UserTestUtils.createUserProfl();
        UserDto.DTL userDto = UserTestUtils.createUser(userProflDto, null);

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        UserProflEntity userProflEntity = entity.getProfl();
        assertNotNull(userProflEntity);
        // TODO: profl 관련 체크
    }

    @Test
    void toEntity_checkEmplym() throws Exception {
        // Given::
        UserEmplymDto userEmplymDto = UserTestUtils.createUserEmplym();
        UserDto.DTL userDto = UserTestUtils.createUser(null, userEmplymDto);

        // When::
        UserEntity entity = userMapstruct.toEntity(userDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        UserEmplymEntity userEmplymEntity = entity.getEmplym();
        assertNotNull(userEmplymEntity);
        // TODO: emplym 관련 체크
    }

    /* ----- */

    /**
     * toDto 검증
     */
    @Test
    void toDto_checkBasic() throws Exception {
        // Given::
        UserEntity userEntity = UserTestUtils.createUserEntity(null, null);

        // When::
        UserDto.DTL dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        // 권한 관련 매핑 검증
        assertEquals(dto.getAuthList().get(0).getAuthCd(), Constant.AUTH_USER);
        assertEquals(dto.getAuthListStr(), Constant.AUTH_USER);
        // 이메일 변환 로직 검증
        assertEquals(dto.getEmailId(), userEntity.getEmail().split("@")[0]);
        assertEquals(dto.getEmailDomain(), userEntity.getEmail().split("@")[1]);
        // 접속 IP 관련 매핑 검증
        assertNotNull(dto.getAcsIpList());
        assertEquals(dto.getAcsIpListStr(), "1.1.1.1,2.2.2.2");
    }

    @Test
    void toDto_checkProfl() throws Exception {
        // Given::
        UserProflEntity userProflEntity = UserTestUtils.createUserProflEntity();
        UserEntity userEntity = UserTestUtils.createUserEntity(userProflEntity, null);

        // When::
        UserDto dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        UserProflDto userProflDto = dto.getProfl();
        assertNotNull(userProflDto);
        // TODO: CHECK PROFL
    }


    @Test
    void toDto_checkEmplym() throws Exception {
        // Given::
        UserEmplymEntity userEmplymEntity = UserTestUtils.createUserEmplymEntity();
        UserEntity userEntity = UserTestUtils.createUserEntity(null, userEmplymEntity);

        // When::
        UserDto dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        UserEmplymDto userEmplymDto = dto.getEmplym();
        assertNotNull(userEmplymDto);
        // TODO: CHECK PROFL
    }
}