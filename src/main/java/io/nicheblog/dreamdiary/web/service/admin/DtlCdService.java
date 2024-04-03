package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCd;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.web.mapstruct.admin.DtlCdMapstruct;
import io.nicheblog.dreamdiary.web.repository.admin.DtlCdRepository;
import io.nicheblog.dreamdiary.web.spec.admin.DtlCdSpec;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DtlCdService
 * <pre>
 *  상세코드 관리 서비스 모듈
 *  ※상세코드(dtl_cd) = 분류코드 하위의 상세코드. 분류코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseManageService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("dtlCdService")
public class DtlCdService
        implements BaseCrudService<DtlCd, DtlCd, DtlCdKey, DtlCdEntity, DtlCdRepository, DtlCdSpec, DtlCdMapstruct>,
                   BaseStateService<DtlCd, DtlCd, DtlCdKey, DtlCdEntity, DtlCdRepository, DtlCdSpec, DtlCdMapstruct> {

    @Resource(name = "dtlCdRepository")
    private DtlCdRepository dtlCdRepository;
    @Resource(name = "dtlCdSpec")
    private DtlCdSpec dtlCdSpec;

    private final DtlCdMapstruct cmmDtlCdMapstruct = DtlCdMapstruct.INSTANCE;

    @Override
    public DtlCdRepository getRepository() {
        return this.dtlCdRepository;
    }

    @Override
    public DtlCdSpec getSpec() {
        return this.dtlCdSpec;
    }

    @Override
    public DtlCdMapstruct getMapstruct() {
        return this.cmmDtlCdMapstruct;
    }

    //
}