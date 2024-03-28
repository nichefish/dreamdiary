package io.nicheblog.dreamdiary.web.model.board;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.*;

import java.io.Serializable;

/**
 * BoardPostKey
 * <pre>
 *  일반게시판 복합키 Dto
 *  (일반게시판 한정하여 contentType = boardCd)
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BoardPostKey
        implements Serializable {

    /**
     * 글번호
     */
    private Integer postNo;
    /**
     * 게시판 코드 (=컨텐츠 타입)
     */
    private String boardCd;

    /** ----- */

    /**
     * BoardPostKey -> BaseClsfKey 변환 
     */    
    public BaseClsfKey getClsfKey() {
        return new BaseClsfKey(this.postNo, this.boardCd);
    }
}
