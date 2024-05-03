package io.nicheblog.dreamdiary.web.service.jrnl.sumry;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryCnEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.sumry.JrnlSumryCnMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.sumry.JrnlSumryCnDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.sumry.JrnlSumryCnRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.sumry.JrnlSumryCnSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * JrnlSumryCnService
 * <pre>
 *  저널 결산 내용 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseClsfService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlSumryCnService")
@Log4j2
public class JrnlSumryCnService
        implements BaseMultiCrudService<JrnlSumryCnDto, JrnlSumryCnDto, Integer, JrnlSumryCnEntity, JrnlSumryCnRepository, JrnlSumryCnSpec, JrnlSumryCnMapstruct> {

    private final JrnlSumryCnMapstruct jrnlSumryCnMapstruct = JrnlSumryCnMapstruct.INSTANCE;

    @Resource(name = "jrnlSumryCnRepository")
    private JrnlSumryCnRepository jrnlSumryCnRepository;
    @Resource(name = "jrnlSumryCnSpec")
    private JrnlSumryCnSpec jrnlSumryCnSpec;

    @Override
    public JrnlSumryCnRepository getRepository() {
        return this.jrnlSumryCnRepository;
    }

    @Override
    public JrnlSumryCnMapstruct getMapstruct() {
        return this.jrnlSumryCnMapstruct;
    }

    @Override
    public JrnlSumryCnSpec getSpec() {
        return this.jrnlSumryCnSpec;
    }

    /**
     * 등록 전처리
     */
    @Override
    public void preRegist(final JrnlSumryCnDto jrnlSumryCn) {
        Integer lastIndex = jrnlSumryCnRepository.findLastIndexByJrnlSumry(jrnlSumryCn.getJrnlSumryNo()).orElse(0);
        jrnlSumryCn.setIdx(lastIndex + 1);
    }
}