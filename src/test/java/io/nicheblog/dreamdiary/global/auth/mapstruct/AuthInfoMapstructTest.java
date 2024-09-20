package io.nicheblog.dreamdiary.global.auth.mapstruct;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.TestConstant;
import io.nicheblog.dreamdiary.global.auth.Auth;
import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.*;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntityTestFactory;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthInfoMapstructTest
 * <pre>
 *  사용자 인증 정보 MapStruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
@Log4j2
class AuthInfoMapstructTest {

    private final AuthInfoMapstruct authInfoMapstruct = AuthInfoMapstruct.INSTANCE;

    /**
     * entity -> dto 검증 :: 기본 속성
     */
    @Test
    void testToDto_checkBasic() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();
        userEntity.setProflImgUrl("test_url");
        // 접속IP : Entity 로딩시 AcsIpList -> AcsIpStrList 변환됨. (@Transient)
        userEntity.setUseAcsIpYn("Y");
        UserAcsIpEntity aa = UserAcsIpEntityTestFactory.createUserAcsIpEntity("1.1.1.1");
        UserAcsIpEntity bb = UserAcsIpEntityTestFactory.createUserAcsIpEntity("2.2.2.2");
        userEntity.setAcsIpList(List.of(aa, bb));
        userEntity.setAcsIpStrList(userEntity.getAcsIpList().stream()
                .map(UserAcsIpEntity::getAcsIp)
                .collect(Collectors.toList()));

        // When::
        AuthInfo dto = authInfoMapstruct.toDto(userEntity);

        // Then::
        assertNotNull(dto);
        assertEquals(dto.getUserId(), TestConstant.TEST_USER);
        assertEquals(dto.getPassword(), TestConstant.TEST_PASSWORD_ENCODED);
        assertEquals(dto.getNickNm(), TestConstant.TEST_NICK_NM);
        assertEquals(dto.getProflImgUrl(), "test_url");
        // 접속 IP
        assertEquals(dto.getUseAcsIpYn(), "Y");
        assertFalse(CollectionUtils.isEmpty(dto.getAcsIpStrList()));
        assertEquals(dto.getAcsIpStrList().size(), 2);
        assertEquals(dto.getAcsIpStrList(), List.of("1.1.1.1", "2.2.2.2"));
    }

    /**
     * entity -> dto 검증 :: 어노테이션 매핑
     */
    @Test
    void testToDto_checkMapping() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();
        // authList
        UserAuthRoleEntity aa = UserAuthRoleEntityTestFactory.getUserAuthRoleEntity(Auth.USER);
        UserAuthRoleEntity bb = UserAuthRoleEntityTestFactory.getUserAuthRoleEntity(Auth.MNGR);
        userEntity.setAuthList(List.of(aa, bb));
        // acntStus
        UserStusEmbed acntStus = UserStusEmbed.builder()
                .lockedYn("N")
                .needsPwReset("Y")
                .cfYn("N")
                .build();
        userEntity.setAcntStus(acntStus);

        // When::
        AuthInfo dto = authInfoMapstruct.toDto(userEntity);

        // Then::
        assertNotNull(dto);
        // authList
        assertFalse(CollectionUtils.isEmpty(dto.getAuthList()));
        assertEquals(dto.getAuthList().size(), 2);
        assertEquals(dto.getAuthList().get(0).getAuthCd(), Constant.AUTH_USER);
        assertEquals(dto.getAuthList().get(1).getAuthCd(), Constant.AUTH_MNGR);
        // acntStus
        assertEquals(dto.getLockedYn(), "N");
        assertEquals(dto.getNeedsPwReset(), "Y");
        assertEquals(dto.getCfYn(), "N");
    }

    /**
     * entity -> dto 검증 :: 어노테이션 매핑 (lstLgnDt, pwChgDt)
     * 로직이 들어간 부분 테스트 분리
     */
    @Test
    void testToDto_checkMapping_stusDtNotNull() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();
        // acntStus - lstLgnDt, pwChgDt = null
        UserStusEmbed acntStus = UserStusEmbed.builder()
                .lstLgnDt(DateUtils.asDate("2000-01-11"))
                .pwChgDt(DateUtils.asDate("2000-02-22"))
                .build();
        userEntity.setAcntStus(acntStus);

        // When::
        AuthInfo dto = authInfoMapstruct.toDto(userEntity);

        // Then::
        assertNotNull(dto);
        // acntStus - lstLgnDt, pwChgDt 대신 regDt 사용
        assertEquals(dto.getLstLgnDt(), DateUtils.asDate("2000-01-11"));
        assertEquals(dto.getPwChgDt(), DateUtils.asDate("2000-02-22"));
    }

    /**
     * entity -> dto 검증 :: 어노테이션 매핑 (lstLgnDt, pwChgDt)
     * 로직이 들어간 부분 테스트 분리
     */
    @Test
    void testToDto_checkMapping_stusDtNull() throws Exception {
        // Given::
        UserEntity userEntity = UserEntityTestFactory.createUser();
        // acntStus - lstLgnDt, pwChgDt = null
        UserStusEmbed acntStus = UserStusEmbed.builder()
                .lstLgnDt(null)
                .pwChgDt(null)
                .build();
        userEntity.setAcntStus(acntStus);
        userEntity.setRegDt(DateUtils.asDate("2000-01-31"));

        // When::
        AuthInfo dto = authInfoMapstruct.toDto(userEntity);

        // Then::
        assertNotNull(dto);
        // acntStus - lstLgnDt, pwChgDt 대신 regDt 사용
        assertEquals(dto.getLstLgnDt(), DateUtils.asDate("2000-01-31"));
        assertEquals(dto.getPwChgDt(), DateUtils.asDate("2000-01-31"));
    }

    /**
     * entity -> dto 검증 :: 사용자 프로필(user_profl) 검증
     */
    @Test
    void testToDto_checkMapping_userProfl() throws Exception {
        // Given::
        UserProflEntity userProflEntity = UserProflEntityTestFactory.createUserProflEntity();
        userProflEntity.setUserProflNo(1);
        UserEntity userEntity = UserEntityTestFactory.createUser(userProflEntity);

        // When::
        AuthInfo dto = authInfoMapstruct.toDto(userEntity);

        // Then::
        assertNotNull(dto);
        // acntStus - lstLgnDt, pwChgDt 대신 regDt 사용
        UserProflDto userProfl = dto.getProfl();
        assertEquals(userProfl.getBrthdy(), "2000-01-01");
        assertEquals(userProfl.getProflCn(), "test_profl_cn");
        assertEquals(dto.getUserProflNo(), 1);
    }

}