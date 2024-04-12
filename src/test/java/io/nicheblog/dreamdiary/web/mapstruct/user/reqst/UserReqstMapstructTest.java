package io.nicheblog.dreamdiary.web.mapstruct.user.reqst;

import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
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
        UserEntity entity = null;
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
        // TODO: PROFL
        // TODO: EMPLYM
    }

    @Test
    void toEntity() {
        // Given
        UserEntity userEntity = UserTestUtils.createUserEntity(null, null);
    }
}