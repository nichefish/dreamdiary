package io.nicheblog.dreamdiary.web.model.cmm.sectn;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * SectnSearchParam
 * <pre>
 *  단락 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostSearchParam
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SectnSearchParam
        extends BaseSearchParam {

    //
}
