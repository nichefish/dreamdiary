package io.nicheblog.dreamdiary.web.mapstruct.user.profl;

import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.test.user.UserTestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UserProflMapstructTest
 * <pre>
 *  사용자 프로필 정보 Mapstruct 매핑 테스트 모듈
 * </pre>
 *
 * @author nichefish
 */
class UserProflMapstructTest {

    private final UserProflMapstruct userProflMapstruct = UserProflMapstruct.INSTANCE;

    /**
     * toEntity 검증
     */
    @Test
    void toEntity_checkBasic() throws Exception {
        // Given::
        UserProflDto userProflDto = UserTestUtils.createUserProfl();

        // When::
        UserProflEntity userProflEntity = userProflMapstruct.toEntity(userProflDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(userProflEntity);
        // 날짜 변환 체크
        assertEquals(userProflEntity.getBrthdy(), DateUtils.asDate("2000-01-01"));
    }

    /* ----- */

    /**
     * toDto 검증
     */
    @Test
    void toDto_checkProfl() throws Exception {

        // Given::
        UserProflEntity userProflEntity = UserTestUtils.createUserProflEntity();

        // When::
        UserProflDto userProflDto = userProflMapstruct.toDto(userProflEntity);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(userProflDto);
        // 날짜 변환 체크
        assertEquals(userProflDto.getBrthdy(), "2000-01-01");
    }
}