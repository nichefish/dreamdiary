package io.nicheblog.dreamdiary.global.intrfc.model.param;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;

/**
 * BasePostSearchParam
 * <pre>
 *  (공통/상속) 게시물 목록 검색 파라미터 Dto
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
public class BasePostSearchParam
        extends BaseSearchParam {


    /** 제목 */
    private String title;
    /** 내용 */
    private String cn;
    /** 글분류 코드 */
    private String ctgrCd;
    /** 중요 여부 */
    private String imprtcYn;
    /** 상단고정 여부 */
    private String fxdYn;
}
