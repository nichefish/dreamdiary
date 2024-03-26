package io.nicheblog.dreamdiary.web.entity.notice;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;

/**
 * NoticeEntity
 * <pre>
 *  공지사항 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BasePostEntity
 */
@Entity
@Table(name = "NOTICE")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE notice SET DEL_YN = 'Y' WHERE POST_NO = ?")
public class NoticeEntity
        extends BasePostEntity {

    /** 필수: 게시물 코드 */
    private static final String BOARD_CD = "NOTICE";
    /** 필수: 글분류 코드 */
    private static final String CTGR_CL_CD = "NOTICE_CTGR_CD";

    /**
     * 글 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_NO")
    @Comment("공지사항 번호")
    private Integer postNo;

    /**
     * 게시판 분류 코드
     */
    @Builder.Default
    @Column(name = "BOARD_CD")
    private String boardCd = BOARD_CD;

    /**
     * 팝업 노출여부
     */
    @Builder.Default
    @Column(name = "POPUP_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("팝업 노출여부")
    private String popupYn = "N";

    /**
     * 조치자(작업자)ID
     */
    @Column(name = "MANAGTR_ID", length = 20)
    private String managtrId;

    /**
     * 조치(작업)일시
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "MANAGT_DT")
    private Date managtDt;

    /**
     * 게시물 태그 목록
     */
    // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    // @JoinColumnsOrFormulas({
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO")),
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "BOARD_CD", referencedColumnName = "BOARD_CD"))
    // })
    // @Fetch(FetchMode.SELECT)
    // @OrderBy("boardTag ASC")
    // @NotFound(action = NotFoundAction.IGNORE)
    // @Comment("게시물 태그 목록")
    // private List<BoardPostTagEntity> tagList;

    /**
     * 파일시스템 참조 목록
     */
    // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    // @JoinColumnsOrFormulas({
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO")),
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "BOARD_CD", referencedColumnName = "BOARD_CD"))
    // })
    // @Fetch(FetchMode.SELECT)
    // @NotFound(action = NotFoundAction.IGNORE)
    // @Comment("파일시스템 참조 목록")
    // private List<FlsysRefEntity> flsysRefList;
}
