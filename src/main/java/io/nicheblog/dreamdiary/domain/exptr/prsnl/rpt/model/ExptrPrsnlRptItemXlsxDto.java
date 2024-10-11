package io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ExptrPrsnlRptItemXlsxDto
 * <pre>
 *  경비 관리 > 경비지출서 월간지출내역 (보고용) 개별 내역 엑셀 다운로드 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ExptrPrsnlRptItemXlsxDto {

    /** 지출일자 */
    private String exptrDt;

    /** 소속 */
    private String cmpyNm;

    /** 이름 */
    private String userNm;

    /** 지출구분코드명 */
    private String exptrTyNm;

    /** 지출내용 */
    private String exptrCn;

    /** 지출금액 */
    private Integer exptrAmt;

    /** 비고 */
    private String exptrRm;

    /** 첨부파일(상세) 번호 */
    private String atchFileAt;

    /** 영수증실물여부 */
    private String orgnlRciptAt;
}
