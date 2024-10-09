package io.nicheblog.dreamdiary.domain._core.auth.service;

import io.nicheblog.dreamdiary.domain._core.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.domain._core.auth.mapstruct.AuthRoleMapstruct;
import io.nicheblog.dreamdiary.domain._core.auth.model.AuthRoleDto;
import io.nicheblog.dreamdiary.domain._core.auth.repository.jpa.AuthRoleRepository;
import io.nicheblog.dreamdiary.domain._core.auth.spec.AuthRoleSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * AuthRoleService
 * <pre>
 *  Spring Security:: 인증 및 권한 처리 관련 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 * @implements BaseReadonlyService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthRoleService
        implements BaseReadonlyService<AuthRoleDto, AuthRoleDto, String, AuthRoleEntity, AuthRoleRepository, AuthRoleSpec, AuthRoleMapstruct> {

    private final AuthRoleRepository authRoleRepository;
    private final AuthRoleSpec authRoleSpec;
    private final AuthRoleMapstruct authRoleMapstruct = AuthRoleMapstruct.INSTANCE;

    @Override
    public AuthRoleRepository getRepository() {
        return this.authRoleRepository;
    }

    @Override
    public AuthRoleSpec getSpec() {
        return this.authRoleSpec;
    }

    @Override
    public AuthRoleMapstruct getMapstruct() {
        return this.authRoleMapstruct;
    }
}