package io.nicheblog.dreamdiary.global.auth.spec;

import io.nicheblog.dreamdiary.global.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.springframework.stereotype.Component;

/**
 * AuthRoleSpec
 * <pre>
 *  게시판 정의 정보 목록 검색인자 세팅 Specification
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
public class AuthRoleSpec
        implements BaseSpec<AuthRoleEntity> {

    //
}
