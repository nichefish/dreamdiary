package io.nicheblog.dreamdiary.domain.jrnl.sbjct.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * JrnlSbjctSearchParam
 * <pre>
 *  저널 주제 목록 검색 파라미터.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JrnlSbjctSearchParam
        extends BasePostSearchParam {
    //
}
