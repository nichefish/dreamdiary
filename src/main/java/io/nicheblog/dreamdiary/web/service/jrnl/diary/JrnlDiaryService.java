package io.nicheblog.dreamdiary.web.service.jrnl.diary;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseClsfService;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.diary.JrnlDiaryMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.diary.JrnlDiaryDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.diary.JrnlDiaryRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.diary.JrnlDiarySpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * JrnlDiaryService
 * <pre>
 *  저널 꿈 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseClsfService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlDiaryService")
@Log4j2
public class JrnlDiaryService
        implements BaseClsfService<JrnlDiaryDto, JrnlDiaryDto, Integer, JrnlDiaryEntity, JrnlDiaryRepository, JrnlDiarySpec, JrnlDiaryMapstruct> {

    private final JrnlDiaryMapstruct jrnlDiaryMapstruct = JrnlDiaryMapstruct.INSTANCE;

    @Resource(name = "jrnlDiaryRepository")
    private JrnlDiaryRepository jrnlDiaryRepository;
    @Resource(name = "jrnlDiarySpec")
    private JrnlDiarySpec jrnlDiarySpec;

    @Override
    public JrnlDiaryRepository getRepository() {
        return this.jrnlDiaryRepository;
    }

    @Override
    public JrnlDiaryMapstruct getMapstruct() {
        return this.jrnlDiaryMapstruct;
    }

    @Override
    public JrnlDiarySpec getSpec() {
        return this.jrnlDiarySpec;
    }
}