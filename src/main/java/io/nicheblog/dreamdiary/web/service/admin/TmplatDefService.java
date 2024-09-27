package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.web.entity.admin.TmplatDefEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.TmplatDefMapstruct;
import io.nicheblog.dreamdiary.web.model.admin.TmplatDefDto;
import io.nicheblog.dreamdiary.web.repository.admin.jpa.TmplatDefRepository;
import io.nicheblog.dreamdiary.web.spec.admin.TmplatDefSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * TmplatDefService
 * <pre>
 *  템플릿 정의 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService, BaseStateService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("tmplatDefService")
@RequiredArgsConstructor
@Log4j2
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
     */
    @Override
    public void preRegist(final TmplatDefDto tmplatDef) {
        if (tmplatDef.getState() == null) tmplatDef.setState(new StateCmpstn());
    }
}