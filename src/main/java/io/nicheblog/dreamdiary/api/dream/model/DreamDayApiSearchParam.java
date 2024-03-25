package io.nicheblog.dreamdiary.api.dream.model;

import lombok.*;

/**
 * DreamDayApiSearchParam
 * <pre>
 *  API:: 꿈 일자 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DreamDayApiSearchParam {

    /**
     * 글분류 코드
     */
    private String ctgrCd;
    /**
     * 제목
     */
    private String title;


}
