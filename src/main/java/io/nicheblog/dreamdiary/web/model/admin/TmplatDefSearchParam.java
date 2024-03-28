package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.web.model.cmm.BaseSearchParam;
import lombok.*;

/**
 * TmplatDefSearchParamDto
 * <pre>
 *  템플릿 정의 목록 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseSearchParamDto
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class TmplatDefSearchParam
        extends BaseSearchParam {

    /**
     * 글분류 코드
     */
    private String ctgrCd;
    /**
     * 제목
     */
    private String postSj;
}
