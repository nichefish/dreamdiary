package io.nicheblog.dreamdiary.web.model.exptr.prsnl.rpt;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * ExptrPrsnlItemDto
 * <pre>
 *  경비 관리 > 경비지출서 월간지출내역 (보고용) 개별 내역 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExptrPrsnlRptItemDto {

    /**
     * 개인 경비 취합 정보 고유 ID (PK)
     */
    private String exptrPrsnlItemNo;
    /**
     * 지출일자
     */
    private String exptrDt;
    /**
     * 지출구분코드
     */
    private String exptrCd;
    /**
     * 지출구분코드명
     */
    private String exptrTyNm;
    /**
     * 지출내용
     */
    private String exptrCn;
    /**
     * 지출금액
     */
    private String exptrAmt;
    /**
     * 비고
     */
    private String exptrRm;
    /**
     * 첨부파일(상세) 번호
     */
    private String atchFileDtlNo;
    /**
     * 영수증실물여부
     */
    private String orgnlRciptYn = "N";
    /**
     * 소속
     */
    private String cmpyNm;
    /**
     * 이름
     */
    private String userNm;

    /* ----- */

    /**
     * 공백여부 판단하여 반환 (날짜 및 항목)
     */
    public Boolean isEmpty() {
        return (this.exptrDt == null) && StringUtils.isEmpty(this.exptrCn);
    }
}
