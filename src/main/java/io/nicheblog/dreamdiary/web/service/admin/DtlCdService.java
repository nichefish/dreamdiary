package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCd;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseManageService;
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
        implements BaseManageService<DtlCd, DtlCd, DtlCdKey, DtlCdEntity, DtlCdRepository, DtlCdSpec, DtlCdMapstruct> {

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

    /**
     * 상세 코드 등록 전처리
     */
    @Override
    public void preRegist(final DtlCd cmmDtlCd) {
        // 사용여부 체크박스 값 세팅
        if (!"Y".equals(cmmDtlCd.getUseYn())) cmmDtlCd.setUseYn("N");
    }

    /**
     * 상세 코드 수정 전처리
     */
    @Override
    public void preModify(final DtlCd cmmDtlCd) {
        // 사용여부 체크박스 값 세팅
        if (!"Y".equals(cmmDtlCd.getUseYn())) cmmDtlCd.setUseYn("N");
    }

    /**
     * 상세 코드 삭제
     */
    @Override
    public Boolean delete(final DtlCdKey key) throws Exception {
        DtlCdEntity cmmDtlCdEntity = this.getDtlEntity(key);       // Entity 레벨 조회
        // 키값이 코드값이므로 delYn 대신 삭제처리
        dtlCdRepository.delete(cmmDtlCdEntity);
        return true;
    }
}