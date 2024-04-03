package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.web.entity.admin.TmplatTxtEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.TmplatTxtMapstruct;
import io.nicheblog.dreamdiary.web.model.admin.TmplatTxtDto;
import io.nicheblog.dreamdiary.web.repository.admin.TmplatTxtRepository;
import io.nicheblog.dreamdiary.web.spec.admin.TmplatTxtSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * TmplatTxtService
 * <pre>
 *  템플릿 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService, BaseStateService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("tmplatTxtService")
@Log4j2
public class TmplatTxtService
        implements BaseCrudService<TmplatTxtDto, TmplatTxtDto, Integer, TmplatTxtEntity, TmplatTxtRepository, TmplatTxtSpec, TmplatTxtMapstruct>,
                   BaseStateService<TmplatTxtDto, TmplatTxtDto, Integer, TmplatTxtEntity, TmplatTxtRepository, TmplatTxtSpec, TmplatTxtMapstruct> {

    @Resource(name = "tmplatTxtRepository")
    private TmplatTxtRepository tmplatTxtRepository;
    @Resource(name = "tmplatTxtSpec")
    private TmplatTxtSpec tmplatTxtSpec;

    private final TmplatTxtMapstruct tmplatTxtMapstruct = TmplatTxtMapstruct.INSTANCE;

    @Override
    public TmplatTxtRepository getRepository() {
        return this.tmplatTxtRepository;
    }

    @Override
    public TmplatTxtSpec getSpec() {
        return this.tmplatTxtSpec;
    }

    @Override
    public TmplatTxtMapstruct getMapstruct() {
        return this.tmplatTxtMapstruct;
    }

    /**
     *
     */
    public TmplatTxtDto getTmplatTxtByTmplatDef(
            final String tmplatDefCd,
            final String ctgrCd
    ) throws Exception {
        Optional<TmplatTxtEntity> entityWrapper = tmplatTxtRepository.findByTmplatDefCdAndCtgrCdAndDefaultYn(tmplatDefCd, ctgrCd, "Y");
        if (entityWrapper.isEmpty()) return null;
        return tmplatTxtMapstruct.toDto(entityWrapper.get());
    }

}