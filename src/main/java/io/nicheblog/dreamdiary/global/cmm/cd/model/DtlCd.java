package io.nicheblog.dreamdiary.global.cmm.cd.model;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
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
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DtlCd
        extends BaseAuditDto {

    /**
     * 목록 순번
     */
    private Long rnum;

    /**
     * 상세코드
     */
    private String dtlCd;
    /**
     * 상세코드이름
     */
    private String dtlCdNm;
    /**
     * 상세코드설명
     */
    private String dtlCdDc;
    /**
     * 분류코드
     */
    private String clCd;
    /**
     * 사용 여부
     */
    private String useYn;
    /**
     * 정렬 순서
     */
    private Integer sortOrdr;

    /**
     * 성공여부
     */
    private Boolean isSuccess;

    /* ---- */

    public DtlCdKey getKey() {
        return new DtlCdKey(this.clCd, this.dtlCd);
    }
}
