package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;

/**
 * MenuSearchParam
 * <pre>
 *  메뉴 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @implements BaseSearchParam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@AllArgsConstructor
public class MenuSearchParam
        extends BaseSearchParam {

    /** 메뉴 유형 코드 */
    private String menuTyCd;
    /** 사용 여부 (Y/N) */
    private String useYn;
}
