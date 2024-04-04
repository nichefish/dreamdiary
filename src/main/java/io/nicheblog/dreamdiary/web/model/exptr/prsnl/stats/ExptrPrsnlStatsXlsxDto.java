package io.nicheblog.dreamdiary.web.model.exptr.prsnl.stats;

import lombok.*;

/**
 * ExptrPrsnlStatsListXlsxDto
 * <pre>
 *  경비 관리 > 경비지출서 > 개인 경비 취합 정보 목록 엑셀 다운로드 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExptrPrsnlStatsXlsxDto {

    /** 구분 */
    private String emplymNm;
    /** 소속 */
    private String cmpyNm;
    /** 이름 */
    private String userNm;

    /** 1월 */
    private Integer jan;
    /** 2월 */
    private Integer feb;
    /** 3월 */
    private Integer mar;
    /** 4월 */
    private Integer apr;
    /** 5월 */
    private Integer may;
    /** 6월 */
    private Integer jun;
    /** 7월 */
    private Integer jul;
    /** 8월 */
    private Integer aug;
    /** 9월 */
    private Integer sep;
    /** 10월 */
    private Integer oct;
    /** 11월 */
    private Integer nov;
    /** 12월 */
    private Integer dec;
}
