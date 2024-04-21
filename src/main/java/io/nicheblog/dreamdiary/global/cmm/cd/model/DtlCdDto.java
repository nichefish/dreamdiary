package io.nicheblog.dreamdiary.global.cmm.cd.model;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstnModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DtlCd
 * <pre>
 *  상세코드(dtlCd) Dto
 *  ※상세코드(dtl_cd) = 분류코드 하위의 상세코드. 분류코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 * @implements StateCmpstnModule
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DtlCdDto
        extends BaseAuditDto
        implements StateCmpstnModule, Identifiable<DtlCdKey> {

    /** 상세코드 */
    private String dtlCd;
    /** 상세코드이름 */
    private String dtlCdNm;
    /** 상세코드설명 */
    private String dc;
    /** 분류코드 */
    private String clCd;

    /* ---- */

    @Override
    public DtlCdKey getKey() {
        return new DtlCdKey(this.clCd, this.dtlCd);
    }

    /* ----- */

    /** 상태 관리 모듈 (위임) */
    public StateCmpstn state;
}
