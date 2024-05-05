package io.nicheblog.dreamdiary.global.cmm.cd.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstnModule;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * ClCdDto
 * <pre>
 *  분류코드(clCd) Dto
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
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
public class ClCdDto
        extends BaseAuditDto
        implements StateCmpstnModule, Identifiable<String> {

    /** 목록 순번 */
    private Long rnum;

    /** 분류코드 */
    private String clCd;
    /** 분류코드이름 */
    private String clCdNm;
    /** 설명 */
    private String dc;
    /** 분류코드 분류 코드 */
    private String clCtgrCd;
    /** 분류코드 분류코드명 */
    private String clCtgrNm;

    /** 상세코드 목록 */
    List<DtlCdDto> dtlCdList;

    /** 상세코드 개수 */
    @Builder.Default
    private Integer dtlCdCnt = 0;

    /* ----- */

    /** 상태 관리 모듈 (위임) */
    public StateCmpstn state;

    @Override
    public String getKey() {
        return this.clCd;
    }
}
