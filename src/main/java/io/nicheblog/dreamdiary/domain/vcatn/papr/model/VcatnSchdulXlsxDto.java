package io.nicheblog.dreamdiary.domain.vcatn.papr.model;

import lombok.*;

/**
 * VcatnSchdulXlsxDto
 * <pre>
 *  휴가일자 엑셀 다운로드 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class VcatnSchdulXlsxDto {

    /** 날짜 */
    private String vcatnSchdul;

    /** 사용자 */
    private String userNm;

    /** 일정 분류 이름 */
    private String vcatnNm;

    /** 소진일 */
    private String vcatnExprDy;

    /** 일정 비고 */
    private String vcatnRm;
}
