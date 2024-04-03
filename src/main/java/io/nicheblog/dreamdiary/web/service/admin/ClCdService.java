package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCd;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.web.mapstruct.admin.ClCdMapstruct;
import io.nicheblog.dreamdiary.web.repository.admin.ClCdRepository;
import io.nicheblog.dreamdiary.web.spec.admin.ClCdSpec;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ClCdService
 * <pre>
 *  분류코드 관리 서비스 모듈
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseManageService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("clCdService")
public class ClCdService
        implements BaseCrudService<ClCd, ClCd, String, ClCdEntity, ClCdRepository, ClCdSpec, ClCdMapstruct>,
                   BaseStateService<ClCd, ClCd, String, ClCdEntity, ClCdRepository, ClCdSpec, ClCdMapstruct> {

    @Resource(name = "clCdRepository")
    private ClCdRepository clCdRepository;
    @Resource(name = "clCdSpec")
    private ClCdSpec clCdSpec;

    private final ClCdMapstruct cmmClCdMapstruct = ClCdMapstruct.INSTANCE;

    @Override
    public ClCdRepository getRepository() {
        return this.clCdRepository;
    }

    @Override
    public ClCdSpec getSpec() {
        return this.clCdSpec;
    }

    @Override
    public ClCdMapstruct getMapstruct() {
        return this.cmmClCdMapstruct;
    }

    //
}