package io.nicheblog.dreamdiary.domain.exptr.prsnl.stats.model;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.domain.user.info.model.UserDto;
import lombok.*;

import java.util.List;

/**
 * ExptrPrsnlStatsDto
 * <pre>
 *  경비 관리 > 경비지출서 > 개인 경비 취합 인원별 집계 정보 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExptrPrsnlStatsDto {

    /** user */
    private UserDto user;

    /** 매달 경비지출서 목록 (1월~12월) (빈 값은 null로 들어감) */
    private List<ExptrPrsnlPaprDto.LIST> paprList;
}
