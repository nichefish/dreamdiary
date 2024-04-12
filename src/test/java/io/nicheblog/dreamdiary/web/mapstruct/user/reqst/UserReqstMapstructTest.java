package io.nicheblog.dreamdiary.web.mapstruct.user.reqst;

import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
import io.nicheblog.dreamdiary.web.test.user.UserTestUtils;
import io.nicheblog.dreamdiary.web.test.user.reqst.UserReqstTestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UserReqstMapstructTest
 * <pre>
 *  사용자 계정 신청 Mapstruct 매핑 테스트 모듈
 * </pre>
 * (toDto는 로직상 존재하지만 세세한 매핑이 중요하지 않음. UserMapstructTest에서 세부 테스트 예정)
 *
 * @author nichefish
 */
class UserReqstMapstructTest {

    private final UserReqstMapstruct userReqstMapstruct = UserReqstMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        UserReqstDto userReqstDto = UserReqstTestUtils.createUserReqst(null, null);

        // When::
        UserEntity entity = userReqstMapstruct.toEntity(userReqstDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        // 이메일 변환 로직
        assertEquals(entity.getEmail(), userReqstDto.getEmailId() + "@" + userReqstDto.getEmailDomain());
        // 접속 IP 관련
        assertEquals(entity.getUseAcsIpYn(), userReqstDto.getUseAcsIpYn());
        assertEquals(entity.getAcsIpList().get(0).getAcsIp(), "1.1.1.1");
        assertEquals(entity.getAcsIpList().get(1).getAcsIp(), "2.2.2.2");
    }

    @Test
    void toEntity_checkProfl() throws Exception {
        // Given::
        UserProflDto userProflDto = UserTestUtils.createUserProfl();
        UserReqstDto userReqstDto = UserReqstTestUtils.createUserReqst(userProflDto, null);

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

    @Test
    void toEntity_checkEmplym() throws Exception {
        // Given::
        UserEmplymDto userEmplymDto = UserTestUtils.createUserEmplym();
        UserReqstDto userReqstDto = UserReqstTestUtils.createUserReqst(null, userEmplymDto);

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