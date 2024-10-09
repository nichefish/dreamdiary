package io.nicheblog.dreamdiary.domain.admin.tmplat.service;

import io.nicheblog.dreamdiary.domain.admin.tmplat.entity.TmplatDefEntity;
import io.nicheblog.dreamdiary.domain.admin.tmplat.mapstruct.TmplatDefMapstruct;
import io.nicheblog.dreamdiary.domain.admin.tmplat.model.TmplatDefDto;
import io.nicheblog.dreamdiary.domain.admin.tmplat.repository.jpa.TmplatDefRepository;
import io.nicheblog.dreamdiary.domain.admin.tmplat.spec.TmplatDefSpec;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * TmplatDefService
 * <pre>
 *  템플릿 정의 관리 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("tmplatDefService")
@RequiredArgsConstructor
public class TmplatDefService
        implements BaseCrudService<TmplatDefDto, TmplatDefDto, Integer, TmplatDefEntity, TmplatDefRepository, TmplatDefSpec, TmplatDefMapstruct>,
                   BaseStateService<TmplatDefDto, TmplatDefDto, Integer, TmplatDefEntity, TmplatDefRepository, TmplatDefSpec, TmplatDefMapstruct> {

    private final TmplatDefRepository tmplatDefRepository;
    private final TmplatDefSpec tmplatDefSpec;
    private final TmplatDefMapstruct tmplatDefMapstruct = TmplatDefMapstruct.INSTANCE;

    @Override
    public TmplatDefRepository getRepository() {
        return this.tmplatDefRepository;
    }

    @Override
    public TmplatDefSpec getSpec() {
        return this.tmplatDefSpec;
    }

    @Override
    public TmplatDefMapstruct getMapstruct() {
        return this.tmplatDefMapstruct;
    }

    /**
     * 등록 전처리 :: override
     * @param dto - 등록할 객체
     */
    @Override
    public void preRegist(final TmplatDefDto dto) {
        if (dto.getState() == null) dto.setState(new StateCmpstn());
    }
}