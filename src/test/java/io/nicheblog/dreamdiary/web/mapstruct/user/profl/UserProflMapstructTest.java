package io.nicheblog.dreamdiary.web.mapstruct.user.profl;

import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.test.user.UserTestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UserProflMapstructTest
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
        UserProflDto userReqstDto = UserTestUtils.createUserProfl();

        // When::
        UserProflEntity entity = userProflMapstruct.toEntity(userReqstDto);

        // Then::
        // 일반 필드는 검증할 필요 없음. 로직이 들어가는 부분에 대하여 테스트 진행
        assertNotNull(entity);
    }
}