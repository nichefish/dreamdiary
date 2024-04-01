package io.nicheblog.dreamdiary.web.model.vcatn.dy;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * VcatnDyDto
 * <pre>
 * 휴가일자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class VcatnDyDto
        extends BaseCrudDto
        implements Comparable<VcatnDyDto> {

    /** 휴가 고유번호 */
    private String vcatnSchdulNo;
    /** 일정자 ID */
    private String userId;
    /** 일정자 이름 */
    private String userNm;
    /** 일정일 */
    private String vcatnDy;
    /** 일정 분류 이름 */
    private String vcatnNm;
    /** 소진일 */
    private Double vcatnExprDy;
    /** 일정 내용 */
    private String resn;
    /** 일정 비고 */
    private String rm;

    /* ----- */

    /**
     * 일정일자 기준 정렬 (오름차순)
     */
    @SneakyThrows
    @Override
    public int compareTo(final @NotNull VcatnDyDto compare) {
        Date thisDate = DateUtils.asDate(this.vcatnDy);
        Date compareDt = DateUtils.asDate(compare.getVcatnDy());
        if (thisDate == null) return -1;
        return thisDate.compareTo(compareDt);
    }
}
