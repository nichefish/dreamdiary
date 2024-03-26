package io.nicheblog.dreamdiary.web.entity.board;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseManageEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * BoardDefEntity
 * <pre>
 *  게시판 정의 정보 Entity
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseManageEntity
 */
@Entity
@Table(name = "BOARD_DEF")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
public class BoardDefEntity
        extends BaseManageEntity {

    /**
     * 게시판 코드 (PK)
     */
    @Id
    @Column(name = "BOARD_CD")
    @Comment("게시판 코드 (key)")
    private String boardCd;

    /**
     * 게시판 이름
     */
    @Column(name = "BOARD_NM")
    @Comment("게시판 이름")
    private String boardNm;

    /**
     * 메뉴번호
     */
    @Column(name = "MENU_NO")
    @Comment("메뉴번호")
    private String menuNo;

    /**
     * 글분류 분류 코드
     */
    @Column(name = "CTGR_CL_CD")
    @Comment("글분류 분류코드")
    private String ctgrClCd;

    /**
     * 삭제여부
     */
    @Builder.Default
    @Column(name = "DEL_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("삭제여부")
    private String delYn = "N";
}