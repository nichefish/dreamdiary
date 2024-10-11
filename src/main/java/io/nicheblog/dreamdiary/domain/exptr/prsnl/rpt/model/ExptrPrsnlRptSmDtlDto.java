package io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.model;

import lombok.*;

/**
 * ExptrPrsnlRptSmDtlDto
 * <pre>
 *  경비 관리 > 개인경비지출서 월간지출내역 (보고용) 집계 계정과목별 합산액 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExptrPrsnlRptSmDtlDto {

    /** 계정과목 코드 */
    private String exptrCd;

    /** 계정과목 이름 */
    private String exptrTyNm;

    /** 계정과목별 총합 */
    private Integer exptrSmAmt;
}
