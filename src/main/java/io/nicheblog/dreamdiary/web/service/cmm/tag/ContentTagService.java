package io.nicheblog.dreamdiary.web.service.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.ContentTagRepository;
import io.nicheblog.dreamdiary.web.spec.cmm.tag.ContentTagSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ContentTagService
 * <pre>
 *  컨텐츠-태그 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("contentTagService")
@Log4j2
public class ContentTagService
        implements BaseCrudService<ContentTagDto, ContentTagDto, Integer, ContentTagEntity, ContentTagRepository, ContentTagSpec, ContentTagMapstruct> {

    private final ContentTagMapstruct tagMapstruct = ContentTagMapstruct.INSTANCE;

    @Resource(name = "contentTagRepository")
    private ContentTagRepository contentTagRepository;
    @Resource(name = "contentTagSpec")
    private ContentTagSpec tagSpec;

    @Override
    public ContentTagRepository getRepository() {
        return this.contentTagRepository;
    }

    @Override
    public ContentTagSpec getSpec() {
        return this.tagSpec;
    }

    @Override
    public ContentTagMapstruct getMapstruct() {
        return this.tagMapstruct;
    }
}
