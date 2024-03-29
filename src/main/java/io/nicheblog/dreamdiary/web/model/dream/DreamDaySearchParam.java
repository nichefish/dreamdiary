package io.nicheblog.dreamdiary.web.model.dream;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;

/**
 * UserSearchParam
 * <pre>
 *  사용자(계정) 정보 목록 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseSearchParam
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DreamDaySearchParam
        extends BaseSearchParam {

    /**
     * 글분류 코드
     */
    private String ctgrCd;
    /**
     * 제목
     */
    private String title;


}
