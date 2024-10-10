package io.nicheblog.dreamdiary.domain.board.post.model;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * BoardPostKey
 * <pre>
 *  게시판 복합키 Dto.
 *  (게시판 한정하여 contentType = boardCd)
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

    /** 글 번호 */
    @Positive
    private Integer postNo;

    /** 게시판 코드 (=컨텐츠 타입) */
    @Size(max = 50)
    private String boardCd;

    /* ----- */

    /**
     * 복합키 객체 반환 (BoardPostKey -> BaseClsfKey 변환)
     * @return {@link BaseClsfKey} -- 글 번호와 콘텐츠 유형을 포함하는 복합키 객체
     */    
    public BaseClsfKey getClsfKey() {
        return new BaseClsfKey(this.postNo, this.boardCd);
    }
}
