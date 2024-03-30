package io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * ExptrPrsnlItemDto
 * <pre>
 *  경비 관리 > 경비지출서 > 경비지출항목 Dto
 *  ※경비지출항목(exptr_prsnl_item) = 경비지출 개별 항목. 경비지출서(exptr_prsnl_papr)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExptrPrsnlItemDto
        extends BaseAuditDto {

    /** 개인 경비 취합 정보 고유 ID (PK) */
    private Integer exptrPrsnlItemNo;
    /**
     * 개인경비 취합 정보 정보 ID
     */
    private String postNo;
    /** 지출일자 */
    private String exptrDt;
    /** 지출구분코드 */
    private String exptrCd;
    /** 지출구분코드명 */
    private String exptrTyNm;
    /** 지출내용 */
    private String exptrCn;
    /** 지출금액 */
    @Builder.Default
    private Integer exptrAmt = 0;
    /** 비고 */
    private String exptrRm;

    /** 첨부파일(상세) 번호 */
    private String atchFileDtlNo;
    /** 영수증 실물제출 여부 (Y/N) */
    @Builder.Default
    private String orgnlRciptYn = "N";

    /** 반려 여부 (Y/N) */
    @Builder.Default
    private String rjectYn = "N";
    /** 반려사유 */
    private String rjectResn;
    
    /* ----- */

    /**
     * 공백여부 판단하여 반환 (날짜 및 항목)
     */
    public Boolean isEmpty() {
        return (this.exptrDt == null) && StringUtils.isEmpty(this.exptrCn);
    }

    /**
     * 영수증 제출여부 class
     */
    public String getOrgnlRciptClass() {
        if ("Y".equals(this.orgnlRciptYn)) return Constant.BS_SUCCESS;
        if ("N".equals(this.orgnlRciptYn)) return Constant.BS_DANGER;
        return Constant.BS_MUTED;
    }
}
