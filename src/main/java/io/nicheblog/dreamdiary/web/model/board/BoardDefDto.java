package io.nicheblog.dreamdiary.web.model.board;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstnModule;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * BoardDefDto
 * <pre>
 *  일반게시판 정의 정보 Dto
 *  ※일반게시판 정의(board_def) = 일반게시판 분류. 일반게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 * @implements StateCmpstnModule
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BoardDefDto
        extends BaseAuditDto
        implements Identifiable<String>, StateCmpstnModule {

    /** 게시판 코드 */
    @NotBlank
    @Size(max = 50)
    private String boardCd;

    /** 게시판 이름 */
    @NotBlank
    private String boardNm;

    /** 메뉴 번호 */
    private String menuNo;

    /** 글분류 분류 코드 */
    @Size(max = 50)
    private String ctgrClCd;

    /** 설명 */
    private String dc;

    /* ----- */

    @Override
    public String getKey() {
        return this.boardCd;
    }

    /** 상태 관리 모듈 (위임) */
    public StateCmpstn state;
}