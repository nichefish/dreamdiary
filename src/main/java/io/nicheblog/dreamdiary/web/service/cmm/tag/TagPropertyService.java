package io.nicheblog.dreamdiary.web.service.cmm.tag;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagPropertyEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.TagPropertyMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagPropertyDto;
import io.nicheblog.dreamdiary.web.repository.cmm.tag.jpa.TagPropertyRepository;
import io.nicheblog.dreamdiary.web.service.jrnl.dream.JrnlDreamService;
import io.nicheblog.dreamdiary.web.spec.cmm.tag.TagPropertySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * TagPropertyService
 * <pre>
 *  태그 속성 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("tagPropertyService")
@RequiredArgsConstructor
@Log4j2
public class TagPropertyService
        implements BaseCrudService<TagPropertyDto, TagPropertyDto, Integer, TagPropertyEntity, TagPropertyRepository, TagPropertySpec, TagPropertyMapstruct> {

    private final TagPropertyRepository tagPropertyRepository;
    private final TagPropertySpec tagPropertySpec;
    private final TagPropertyMapstruct tagPropertyMapstruct = TagPropertyMapstruct.INSTANCE;

    private final ContentTagService contentTagService;
    private final JrnlDreamService jrnlDreamService;
    private final JPAQueryFactory queryFactory;

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
