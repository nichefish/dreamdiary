package io.nicheblog.dreamdiary.web.model.cmm;

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

    /**
     * 글분류 코드
     */
    private String ctgrCd;

    /**
     * 제목
     */
    private String title;


}
