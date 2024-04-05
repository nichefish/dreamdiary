package io.nicheblog.dreamdiary.web.service.vcatn.papr;

import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.papr.VcatnSchdulMapstruct;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnSchdulDto;
import io.nicheblog.dreamdiary.web.repository.vcatn.VcatnSchdulRepository;
import io.nicheblog.dreamdiary.web.service.vcatn.stats.VcatnStatsYyService;
import io.nicheblog.dreamdiary.web.spec.vcatn.VcatnSchdulSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * VcatnPaprService
 * <pre>
 *  휴가계획서 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("vcatnSchdulService")
@Log4j2
public class VcatnSchdulService
        implements BaseCrudService<VcatnSchdulDto, VcatnSchdulDto, Integer, VcatnSchdulEntity, VcatnSchdulRepository, VcatnSchdulSpec, VcatnSchdulMapstruct> {

    @Resource(name = "vcatnSchdulRepository")
    private VcatnSchdulRepository vcatnSchdulRepository;
    @Resource(name = "vcatnSchdulSpec")
    private VcatnSchdulSpec vcatnSchdulSpec;

    private final VcatnSchdulMapstruct vcatnSchdulMapstruct = VcatnSchdulMapstruct.INSTANCE;

    @Resource(name = "vcatnStatsYyService")
    private VcatnStatsYyService vcatnStatsYyService;
    @Resource(name = "cdService")
    public CdService cmmCdService;

    @Override
    public VcatnSchdulRepository getRepository() {
        return this.vcatnSchdulRepository;
    }

    @Override
    public VcatnSchdulSpec getSpec() {
        return this.vcatnSchdulSpec;
    }

    @Override
    public VcatnSchdulMapstruct getMapstruct() {
        return this.vcatnSchdulMapstruct;
    }

    //
}
