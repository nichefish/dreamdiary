package io.nicheblog.dreamdiary.global.intrfc.model.param;

import lombok.*;

/**
 * BasePostSearchParam
 * <pre>
 *  (공통/상속) 게시물 목록 검색 파라미터 Dto.
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfSearchParam
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class BasePostSearchParam
        extends BaseClsfSearchParam {

    /** 제목 */
    protected String title;

    /** 내용 */
    protected String cn;

    /** 글분류 코드 */
    protected String ctgrCd;

    /** 중요 여부 (Y/N) */
    protected String imprtcYn;

    /** 상단고정 여부 (Y/N) */
    protected String fxdYn;
}
