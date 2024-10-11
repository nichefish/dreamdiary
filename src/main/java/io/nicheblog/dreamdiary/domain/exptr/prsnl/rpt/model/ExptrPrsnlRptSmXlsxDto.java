package io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.model;

import io.nicheblog.dreamdiary.global.model.CmmStus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ExptrPrsnlRptSmXlsxDto
 * <pre>
 *  경비 관리 > 경비지출서 월간지출내역 (보고용) 집계 엑셀 다운로드 Dto
 * </pre>
 * 경비 관리 > 경비지출서 월간지출내역 (보고용) 집계 Dto
 * TODO: 아직 미적용 = 적용 예정
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ExptrPrsnlRptSmXlsxDto {

    /** 개별 계정항목별 총합 */
    List<ExptrPrsnlRptSmDtlDto> itemList;

    /** 소속 */
    private String cmpyNm;
    /** 이름 */
    private String userNm;
    /** 은행 */
    private String acntBank;
    /** 계좌번호 */
    private String acntNo;

    /** 금액 합계 */
    private Integer totAmt;
    /** 항목 건수 */
    private Integer itemCnt = 0;
    /** 영수증 첨부 건수 */
    private Integer atchRciptCnt = 0;
    /** 영수증 제출 건수 */
    private Integer orgnlRciptCnt = 0;
    /** 영수증 불필요 건수 */
    private Integer orgnlRciptNotNeededCnt = 0;

    /** 영수증 스캔본 첨부 상태 */
    private CmmStus atchRciptStus;
    /** 영수증 원본 제출 상태 */
    private CmmStus orgnlRciptStus;

    /* ----- */

    /**
     * 사용자 정보 세팅
     */
    // public void setUserInfo(final UserDto user) {
    //     UserInfoDto userInfo = user.getUserInfo();
    //     if (userInfo == null) return;
    //     this.cmpyNm = userInfo.getCmpyNm();
    //     this.userNm = userInfo.getUserNm();
    //     this.acntBank = userInfo.getAcntBank();
    //     this.acntNo = userInfo.getAcntNo();
    // }
}
