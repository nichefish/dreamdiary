package io.nicheblog.dreamdiary.web.mapstruct.user.emplym;

import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntityTestFactory;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDtoTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UserEmplymMapstructTest
 * <pre>
 *  사용자 인사정보 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
@ActiveProfiles("test")
class UserEmplymMapstructTest {

    private final UserEmplymMapstruct userEmplymMapstruct = UserEmplymMapstruct.INSTANCE;

    /**
     * entity -> dto 검증
     */
    @Test
    void testToDto_checkEmplym() throws Exception {

        // Given::
        UserEmplymEntity userEmplymEntity = UserEmplymEntityTestFactory.createUserEmplymEntity();

        // When::
        UserEmplymDto userEmplymDto = userEmplymMapstruct.toDto(userEmplymEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(userEmplymDto);
        // 날짜 변환 체크
        assertEquals(userEmplymEntity.getEcnyDt(), DateUtils.asDate("2000-01-01"));
        assertEquals(userEmplymEntity.getRetireDt(), DateUtils.asDate("2000-01-01"));
        // 이메일 변환 로직
        assertEquals(userEmplymEntity.getEmplymEmail(), userEmplymDto.getEmplymEmailId() + "@" + userEmplymDto.getEmplymEmailDomain());
    }

    /* ----- */

    /**
     * toEntity 검증
     */
    @Test
    void testToEntity_checkBasic() throws Exception {
        // Given::
        UserEmplymDto userEmplymDto = UserEmplymDtoTestFactory.createUserEmplym();

        // When::
        UserEmplymEntity userEmplymEntity = userEmplymMapstruct.toEntity(userEmplymDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(userEmplymEntity);
        // 날짜 변환 체크
        assertEquals(userEmplymEntity.getEcnyDt(), DateUtils.asDate("2000-01-01"));
        assertEquals(userEmplymEntity.getRetireDt(), DateUtils.asDate("2000-01-01"));
        // 이메일 변환 로직
        assertEquals(userEmplymEntity.getEmplymEmail(), userEmplymDto.getEmplymEmailId() + "@" + userEmplymDto.getEmplymEmailDomain());
    }

}