package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCd;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseManageService;
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
        implements BaseManageService<ClCd, ClCd, String, ClCdEntity, ClCdRepository, ClCdSpec, ClCdMapstruct> {

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

    /**
     * 분류 코드 등록 전처리
     */
    @Override
    public void preRegist(final ClCd cmmClCd) {
        // 사용여부 체크박스 값 세팅
        if (!"Y".equals(cmmClCd.getUseYn())) cmmClCd.setUseYn("N");
    }

    /**
     * 분류 코드 수정 전처리
     */
    @Override
    public void preModify(final ClCd cmmClCd) {
        // 사용여부 체크박스 값 세팅
        if (!"Y".equals(cmmClCd.getUseYn())) cmmClCd.setUseYn("N");
    }

    /**
     * 분류 코드 삭제
     */
    @Override
    public Boolean delete(final String key) throws Exception {
        ClCdEntity cmmClCdEntity = this.getDtlEntity(key);       // Entity 레벨 조회
        // 키값이 코드값이므로 delYn 대신 삭제처리
        clCdRepository.delete(cmmClCdEntity);
        return true;
    }
}