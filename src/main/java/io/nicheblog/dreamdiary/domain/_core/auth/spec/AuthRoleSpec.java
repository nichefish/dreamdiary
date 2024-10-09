package io.nicheblog.dreamdiary.domain._core.auth.spec;

import io.nicheblog.dreamdiary.domain._core.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.embed.BaseStateSpec;
import org.springframework.stereotype.Component;

/**
 * AuthRoleSpec
 * <pre>
 *  권한 정보 목록 검색인자 세팅 Specification.
 * </pre>
 *
 * @author nichefish
 * @implements BaseStateSpec - 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
public class AuthRoleSpec
        implements BaseStateSpec<AuthRoleEntity> {

    //
}
