package io.nicheblog.dreamdiary.web.model.jrnl.sbjct;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

/**
 * JrnlSbjctSearchParam
 * <pre>
 *  일반게시판 게시물 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostSearchParam
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class JrnlSbjctSearchParam
        extends BasePostSearchParam {

    /** 게시판 코드 */
    private String boardCd;

    /* ----- */

    /** 
     * 게시판 코드 <-> 컨텐츠 타입 변환 
     */
    public String getBoardCd() {
        if (StringUtils.isEmpty(boardCd)) return this.getContentType();
        return this.boardCd;
    }
}
