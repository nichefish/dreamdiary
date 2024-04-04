package io.nicheblog.dreamdiary.global.auth.service;

import io.nicheblog.dreamdiary.global.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.global.auth.mapstruct.AuthRoleMapstruct;
import io.nicheblog.dreamdiary.global.auth.model.AuthRoleDto;
import io.nicheblog.dreamdiary.global.auth.repository.AuthRoleRepository;
import io.nicheblog.dreamdiary.global.auth.spec.AuthRoleSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AuthRoleService
 * <pre>
 *  Spring Security:: 인증 및 권한 처리 관련 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service
@Log4j2
public class AuthRoleService
        implements BaseReadonlyService<AuthRoleDto, AuthRoleDto, String, AuthRoleEntity, AuthRoleRepository, AuthRoleSpec, AuthRoleMapstruct> {

    private final AuthRoleMapstruct authRoleMapstruct = AuthRoleMapstruct.INSTANCE;

    @Resource(name = "authRoleRepository")
    private AuthRoleRepository authRoleRepository;
    @Resource(name = "authRoleSpec")
    private AuthRoleSpec authRoleSpec;

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