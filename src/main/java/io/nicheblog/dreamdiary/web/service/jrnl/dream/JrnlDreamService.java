package io.nicheblog.dreamdiary.web.service.jrnl.dream;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseClsfService;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.dream.JrnlDreamMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.dream.JrnlDreamRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.dream.JrnlDreamSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * JrnlDreamService
 * <pre>
 *  저널 꿈 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseClsfService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlDreamService")
@Log4j2
public class JrnlDreamService
        implements BaseClsfService<JrnlDreamDto, JrnlDreamDto, Integer, JrnlDreamEntity, JrnlDreamRepository, JrnlDreamSpec, JrnlDreamMapstruct> {

    private final JrnlDreamMapstruct jrnlDreamMapstruct = JrnlDreamMapstruct.INSTANCE;

    @Resource(name = "jrnlDreamRepository")
    private JrnlDreamRepository jrnlDreamRepository;
    @Resource(name = "jrnlDreamSpec")
    private JrnlDreamSpec jrnlDreamSpec;

    @Override
    public JrnlDreamRepository getRepository() {
        return this.jrnlDreamRepository;
    }

    @Override
    public JrnlDreamMapstruct getMapstruct() {
        return this.jrnlDreamMapstruct;
    }

    @Override
    public JrnlDreamSpec getSpec() {
        return this.jrnlDreamSpec;
    }

    /**
     * 등록 전처리
     */
    @Override
    public void preRegist(final JrnlDreamDto jrnlDream) {
        if (!"Y".equals(jrnlDream.getElseDreamYn())) {
            // 인덱스(정렬순서) 처리
            Integer lastIndex = jrnlDreamRepository.findLastIndexByJrnlDay(jrnlDream.getJrnlDayNo()).orElse(0);
            jrnlDream.setIdx(lastIndex + 1);
        }
    }
}