package io.nicheblog.dreamdiary.web.entity.schdul;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbedModule;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbedModule;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SchdulEntity
 * <pre>
 *  일정 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "schdul")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE schdul SET del_yn = 'Y' WHERE post_no = ?")
public class SchdulEntity
        extends BasePostEntity
        implements CommentEmbedModule, TagEmbedModule {

    @PostLoad
    private void onLoad() {
        // 코드 이름 세팅
        if (this.schdulCdInfo != null) this.schdulNm = this.schdulCdInfo.getDtlCdNm();
        if (!CollectionUtils.isEmpty(this.prtcpntList)) {
            this.prtcpntStr = this.prtcpntList.stream()
                    .filter(entity -> entity.getUser() != null)
                    .map(entity -> entity.getUser().getNickNm())
                    .collect(Collectors.joining(", "));
        }
    }

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.SCHDUL;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CD";

    /** 글 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("공지사항 번호")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type", columnDefinition = "VARCHAR(50) DEFAULT 'SCHDUL'")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

    /** 글분류 코드 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'SCHDUL_CD'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ctgr_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("일정 분류 코드 정보")
    protected DtlCdEntity ctgrCdInfo;

    /* ----- */

    /** 출처 (ex.KASI) */
    @Column(name = "src")
    @Comment("출처 (ex.KASI) ")
    private String src;

    /** 일정 코드 */
    @Column(name = "schdul_cd")
    @Comment("일정분류코드")
    private String schdulCd;

    /** 일정 코드 정보 (복합키 조인) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.SCHDUL_CD + "\'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "schdul_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("일정 코드 정보")
    private DtlCdEntity schdulCdInfo;

    /** 일정 코드명 */
    @Transient
    private String schdulNm;

    /** 시작일 */
    @Column(name = "bgn_dt")
    @Comment("시작일")
    private Date bgnDt;

    /** 일정 종료일 */
    @Column(name = "end_dt")
    @Comment("종료일")
    private Date endDt;

    /** 개인일정 여부 (Y/N) */
    @Builder.Default
    @Column(name = "prvt_yn")
    @Comment("개인일정 여부 (Y/N)")
    private String prvtYn = "N";

    /** 참여자 정보 */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no")
    @Fetch(FetchMode.SELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("참여자 정보")
    private List<SchdulPrtcpntEntity> prtcpntList;

    @Transient
    private String prtcpntStr;

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentEmbed comment;
    /** 태그 정보 모듈 (위임) */
    @Embedded
    public TagEmbed tag;
}
