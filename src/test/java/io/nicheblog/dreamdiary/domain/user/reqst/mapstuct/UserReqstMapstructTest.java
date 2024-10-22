package io.nicheblog.dreamdiary.domain.user.reqst.mapstuct;

import io.nicheblog.dreamdiary.domain.user.emplym.entity.UserEmplymEntity;
import io.nicheblog.dreamdiary.domain.user.emplym.model.UserEmplymDtoTestFactory;
import io.nicheblog.dreamdiary.domain.user.info.entity.UserEntity;
import io.nicheblog.dreamdiary.domain.user.info.model.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.domain.user.info.model.profl.UserProflDto;
import io.nicheblog.dreamdiary.domain.user.profl.entity.UserProflEntity;
import io.nicheblog.dreamdiary.domain.user.profl.model.UserProflDtoTestFactory;
import io.nicheblog.dreamdiary.domain.user.reqst.mapstruct.UserReqstMapstruct;
import io.nicheblog.dreamdiary.domain.user.reqst.model.UserReqstDto;
import io.nicheblog.dreamdiary.domain.user.reqst.model.UserReqstDtoTestFactory;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UserReqstMapstructTest
 * <pre>
 *  사용자 계정 신청 Mapstruct 매핑 테스트 모듈
 * </pre>
 * (toDto는 로직상 존재하지만 여기서는 세세한 매핑이 중요하지 않음. UserMapstructTest에서 테스트 예정)
 *
 * @author nichefish
 */
@ActiveProfiles("test")
class UserReqstMapstructTest {

    private final UserReqstMapstruct userReqstMapstruct = UserReqstMapstruct.INSTANCE;

    /**
     * dto -> entity 변환 검증 :: 기본 체크
     */
    @Test
    void testToEntity_checkBasic() throws Exception {
        // Given::
`        UserReqstDto userReqstDto = UserReqstDtoTestFactory.create();

        // When::
        UserEntity entity = userReqstMapstruct.toEntity(userReqstDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        // 이메일 변환 로직
        assertEquals(entity.getEmail(), userReqstDto.getEmailId() + "@" + userReqstDto.getEmailDomain());
    }

    /**
     * dto -> entity 변환 검증 :: 접속 IP 관련
     */
    @Test
    void testToEntity_checkAcsIp() throws Exception {
        // Given::
        UserReqstDto userReqstDto = UserReqstDtoTestFactory.create();
        // 접속 IP 세팅
        userReqstDto.setUseAcsIpYn("Y");
        userReqstDto.setAcsIpListStr("[{\"value\":\"1.1.1.1\"},{\"value\":\"2.2.2.2\"}]");

        // When::
        UserEntity entity = userReqstMapstruct.toEntity(userReqstDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        // 접속 IP 관련
        assertEquals(entity.getUseAcsIpYn(), userReqstDto.getUseAcsIpYn());
        assertEquals(entity.getAcsIpList().get(0).getAcsIp(), "1.1.1.1");
        assertEquals(entity.getAcsIpList().get(1).getAcsIp(), "2.2.2.2");
    }

    /** 
     * dto -> entity 변환 검증 :: 사용자 프로필 정보 관련
     */
    @Test
    void testToEntity_checkProfl() throws Exception {
        // Given::
        UserProflDto userProflDto = UserProflDtoTestFactory.create();
        UserReqstDto userReqstDto = UserReqstDtoTestFactory.create(userProflDto);

        // When::
        UserEntity entity = userReqstMapstruct.toEntity(userReqstDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        UserProflEntity userProflEntity = entity.getProfl();
        assertNotNull(userProflEntity);
        // 날짜 변환 체크
        assertEquals(userProflEntity.getBrthdy(), DateUtils.asDate("2000-01-01"));
    }

    /**
     * dto -> entity 변환 검증 :: 사용자 인사정보 관련
     */
    @Test
    void testToEntity_checkEmplym() throws Exception {
        // Given::
        UserEmplymDto userEmplymDto = UserEmplymDtoTestFactory.create();
        UserReqstDto userReqstDto = UserReqstDtoTestFactory.create(userEmplymDto);

        // When::
        UserEntity entity = userReqstMapstruct.toEntity(userReqstDto);

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