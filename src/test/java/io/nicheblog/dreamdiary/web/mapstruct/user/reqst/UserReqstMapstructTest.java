package io.nicheblog.dreamdiary.web.mapstruct.user.reqst;

import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
import io.nicheblog.dreamdiary.web.test.user.UserTestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * UserReqstMapstructTest
 * <pre>
 *  사용자 계정 신청 Mapstruct 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
class UserReqstMapstructTest {

    private final UserReqstMapstruct userReqstMapstruct = UserReqstMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() {
        // Given
        UserReqstDto userReqstDto = UserTestUtils.createUserReqst(null, null);

        // When
        UserEntity entity;
        try {
            entity = userReqstMapstruct.toEntity(userReqstDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Then
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
    void toEntity_checkProfl() {
        // Given
        UserProflDto userProflDto = UserTestUtils.createUserProfl();
        UserReqstDto userReqstDto = UserTestUtils.createUserReqst(userProflDto, null);

        // When
        UserEntity entity;
        try {
            entity = userReqstMapstruct.toEntity(userReqstDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Then
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
        UserProflEntity userProflEntity = entity.getProfl();
        assertNotNull(userProflEntity);
        // TODO: profl 관련 체크
    }

    @Test
    void toEntity_checkEmplym() {
        // Given
        UserEmplymDto userEmplymDto = UserTestUtils.createUserEmplym();
        UserReqstDto userReqstDto = UserTestUtils.createUserReqst(null, userEmplymDto);

        // When
        UserEntity entity;
        try {
            entity = userReqstMapstruct.toEntity(userReqstDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Then
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
    void toDto() {
        // Given
        UserEntity userEntity = UserTestUtils.createUserEntity(null, null);

        // When
        UserReqstDto dto;
        try {
            dto = userReqstMapstruct.toDto(userEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Then
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(dto);
        // 권한 관련

        // 이메일 변환 로직
        assertEquals(dto.getEmailId(), userEntity.getEmail().split("@")[0]);
        assertEquals(dto.getEmailDomain(), userEntity.getEmail().split("@")[1]);
        // 접속 IP 관련
        assertNotNull(dto.getAcsIpList());
        // 목록 검증? 어케하지?
        assertEquals(dto.getAcsIpListStr(), "1.1.1.1,2.2.2.2");
    }
}