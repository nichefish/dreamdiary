package io.nicheblog.dreamdiary.web.mapstruct.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntityTestFactory;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntityTestFactory;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntityTestFactory;
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
 * UserMapstructTest
 * <pre>
 *  사용자 계정 신청 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@Log4j2
class UserMapstructTest {

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;

    /**
     * toDto 검증
     */
    @Test
    void toDto_checkAuditor() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUserEntity();

        // When::
        UserDto.DTL dto = userMapstruct.toDto(userEntity);

        // Then::
        assertNotNull(dto);
        assertEquals(dto.getRegstrId(), "test_reg_user");
        assertEquals(dto.getRegstrNm(), "test_reg_nick_nm");
        assertEquals(dto.getRegDt(), "2000-01-01 00:00:00");
        assertEquals(dto.getMdfusrId(), "test_mdf_user");
        assertEquals(dto.getMdfusrNm(), "test_reg_nick_nm");
        assertEquals(dto.getMdfDt(), "2000-01-01 00:00:00");
    }

    @Test
    void toDto_checkBasic() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUserEntity();

        // When::
        UserDto.DTL dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        // 권한 관련 매핑 검증
        assertFalse(CollectionUtils.isEmpty(dto.getAuthList()));
        assertEquals(dto.getAuthList().size(), 2);
        assertEquals(dto.getAuthList().get(0).getAuthCd(), Constant.AUTH_USER);
        assertEquals(dto.getAuthList().get(1).getAuthCd(), Constant.AUTH_MNGR);
        // 이메일 변환 로직 검증
        assertEquals(dto.getEmailId(), userEntity.getEmail().split("@")[0]);
        assertEquals(dto.getEmailDomain(), userEntity.getEmail().split("@")[1]);
        // 접속 IP 관련 매핑 검증
        assertNotNull(dto.getAcsIpList());
        assertNotNull(dto.getAcsIpList().get(0).getAcsIp(), "1.1.1.1");
        assertNotNull(dto.getAcsIpList().get(0).getAcsIp(), "2.2.2.2");
        assertEquals(dto.getAcsIpListStr(), "1.1.1.1,2.2.2.2");
    }

    @Test
    void toDto_checkProfl() throws Exception {
        // Given::
        UserProflEntity userProflEntity = UserProflEntityTestFactory.createUserProflEntity();
        UserEntity userEntity = UserEntityTestFactory.createUserEntity(userProflEntity);

        // When::
        UserDto dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        UserProflDto userProflDto = dto.getProfl();
        assertNotNull(userProflDto);
        // 날짜 변환 체크
        assertEquals(userProflDto.getBrthdy(), "2000-01-01");
    }


    @Test
    void toDto_checkEmplym() throws Exception {
        // Given::
        UserEmplymEntity userEmplymEntity = UserEmplymEntityTestFactory.createUserEmplymEntity();
        UserEntity userEntity = UserEntityTestFactory.createUserEntity(userEmplymEntity);

        // When::
        UserDto dto = userMapstruct.toDto(userEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        UserEmplymDto userEmplymDto = dto.getEmplym();
        assertNotNull(userEmplymDto);
        // 날짜 변환 체크
        assertEquals(userEmplymDto.getEcnyDt(), "2000-01-01");
        assertEquals(userEmplymDto.getRetireDt(), "2000-01-01");
        // 이메일 변환 로직
        assertEquals(userEmplymDto.getEmplymEmailId(), userEmplymEntity.getEmplymEmail().split("@")[0]);
        assertEquals(userEmplymDto.getEmplymEmailDomain(), userEmplymEntity.getEmplymEmail().split("@")[1]);
    }

    /**
     * toListDto 검증
     */
    @Test
    void toListDto_checkAuditor() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUserEntity();

        // When::
        UserDto.LIST dto = userMapstruct.toListDto(userEntity);

        // Then::
        assertNotNull(dto);
        assertEquals(dto.getRegstrId(), "test_reg_user");
        assertEquals(dto.getRegstrNm(), "test_reg_nick_nm");
        assertEquals(dto.getRegDt(), "2000-01-01 00:00:00");
        assertEquals(dto.getMdfusrId(), "test_mdf_user");
        assertEquals(dto.getMdfusrNm(), "test_reg_nick_nm");
        assertEquals(dto.getMdfDt(), "2000-01-01 00:00:00");
    }

    /* ----- */

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        UserDto.DTL userDto = UserDtoTestFactory.createUser(null, null);

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
        UserDto.DTL userDto = UserDtoTestFactory.createUser(userProflDto, null);

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
        UserDto.DTL userDto = UserDtoTestFactory.createUser(null, userEmplymDto);

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