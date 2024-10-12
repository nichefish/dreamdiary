package io.nicheblog.dreamdiary.domain.vcatn.stats.model;

import lombok.*;

/**
 * VcatnStatsListXlsxDto
 * <pre>
 *  휴가계획서 목록 엑셀 다운로드 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class VcatnStatsListXlsxDto {

    /** 직원명 */
    private String userNm;

    /** 입사일 */
    private String ecnyDt;

    /** 산정기준년도 */
    private String statsDt;

    /** 근속년수 */
    private String cnwkYy;

    /** 기본연차 */
    private String bsYryc;

    /** 근속 추가연차 */
    private String cnwkYryc;

    /** 프로젝트 추가연차 */
    private String prjctYryc;

    /** 안식주 */
    private String refreshYryc;


    /** 총연차 */
    private String totYryc;

    /** 소진연차 */
    private String exhsYryc;

    /** 잔여연차 */
    private String remndrYryc;
}
