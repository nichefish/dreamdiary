package io.nicheblog.dreamdiary.web.entity.board;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseManageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "board_def")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
public class BoardDefEntity
        extends BaseManageEntity {

    /**
     * 게시판 코드 (PK)
     */
    @Id
    @Column(name = "board_cd")
    @Comment("게시판 코드 (key)")
    private String boardCd;

    /**
     * 게시판 이름
     */
    @Column(name = "board_nm")
    @Comment("게시판 이름")
    private String boardNm;

    /**
     * 메뉴번호
     */
    @Column(name = "menu_no")
    @Comment("메뉴번호")
    private String menuNo;

    /**
     * 글분류 분류 코드
     */
    @Column(name = "ctgr_cl_cd")
    @Comment("글분류 분류코드")
    private String ctgrClCd;
}