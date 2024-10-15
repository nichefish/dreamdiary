package io.nicheblog.dreamdiary.domain.vcatn.papr.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * VcatnSchdulDto
 * <pre>
 *  휴가계획 상세일정 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class VcatnSchdulDto
        extends BaseCrudDto
        implements Identifiable<Integer> {

    /** 휴가 고유번호 (PK) */
    private Integer vcatnSchdulNo;

    /** 글 번호 */
    private String refPostNo;

    /** 사용자 ID  */
    private String userId;

    /** 사용자 이름  */
    private String userNm;

    /** 휴가 시작일 */
    private String bgnDt;

    /** 휴가 종료일 */
    private String endDt;

    /** 휴가 분류 코드 */
    private String vcatnCd;

    /** 휴가 분류 코드 이름 */
    private String vcatnNm;

    /** 휴가 사유 */
    private String resn;

    /** 비고 */
    private String rm;

    /** 처리 성공여부  */
    private Boolean isSuccess;

    /* ----- */

    @Override
    public Integer getKey() {
        return this.vcatnSchdulNo;
    }
}
