package io.nicheblog.dreamdiary.domain.jrnl.sbjct.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

/**
 * JrnlSbjctSearchParam
 * <pre>
 *  일반게시판 게시물 검색 파라미터.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JrnlSbjctSearchParam
        extends BasePostSearchParam {
    //
}
