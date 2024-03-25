package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.web.model.cmm.BaseSearchParam;
import lombok.*;

/**
 * ClCdSearchParam
 * <pre>
 *  분류코드 목록 검색 파라미터 Dto
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
public class ClCdSearchParam
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
