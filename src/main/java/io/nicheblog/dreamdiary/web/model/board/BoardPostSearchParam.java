package io.nicheblog.dreamdiary.web.model.board;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;

/**
 * BoardPostSearchParam
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
public class BoardPostSearchParam
        extends BasePostSearchParam {

    /** 게시판 코드 */
    @Size(max = 50)
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
