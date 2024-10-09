package io.nicheblog.dreamdiary.domain._core.tag.service;

import io.nicheblog.dreamdiary.domain._core.tag.entity.TagPropertyEntity;
import io.nicheblog.dreamdiary.domain._core.tag.mapstruct.TagPropertyMapstruct;
import io.nicheblog.dreamdiary.domain._core.tag.model.TagPropertyDto;
import io.nicheblog.dreamdiary.domain._core.tag.repository.jpa.TagPropertyRepository;
import io.nicheblog.dreamdiary.domain._core.tag.spec.TagPropertySpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * TagPropertyService
 * <pre>
 *  태그 속성 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("tagPropertyService")
@RequiredArgsConstructor
@Log4j2
public class TagPropertyService
        implements BaseCrudService<TagPropertyDto, TagPropertyDto, Integer, TagPropertyEntity, TagPropertyRepository, TagPropertySpec, TagPropertyMapstruct> {

    private final TagPropertyRepository tagPropertyRepository;
    private final TagPropertySpec tagPropertySpec;
    private final TagPropertyMapstruct tagPropertyMapstruct = TagPropertyMapstruct.INSTANCE;

    @Override
    public TagPropertyRepository getRepository() {
        return this.tagPropertyRepository;
    }
    @Override
    public TagPropertySpec getSpec() {
        return this.tagPropertySpec;
    }
    @Override
    public TagPropertyMapstruct getMapstruct() {
        return this.tagPropertyMapstruct;
    }
}
