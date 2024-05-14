package io.nicheblog.dreamdiary.web.model.schdul;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * SchdulSearchParam
 * <pre>
 *  일정 목록 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseSearchParam
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SchdulSearchParam
        extends BaseSearchParam {

    /** 조회시작일자 */
    private String bgnDt;
    /** 조회종료일자 */
    private String endDt;

    /** 내 정보 표시여부 */
    private String myPaprChked;
    /** 휴가 표시여부 */
    private String vcatnChked;
    /** 내부일정 정보 표시여부 */
    private String indtChked;
    /** 외근 정보 표시여부 */
    private String outdtChked;
    /** 재택근무 정보 표시여부 */
    private String tlcmmtChked;
    /** 개인용 정보 표시여부 */
    private String prvtChked;

    /**
     * 검색 키워드
     */
    private String searchKeyword;
}
