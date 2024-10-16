package io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.global._common._clsf.comment.entity.embed.CommentEmbed;
import io.nicheblog.dreamdiary.global._common._clsf.comment.entity.embed.CommentEmbedModule;
import io.nicheblog.dreamdiary.global._common._clsf.managt.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global._common._clsf.managt.entity.embed.ManagtEmbedModule;
import io.nicheblog.dreamdiary.global._common._clsf.tag.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global._common._clsf.tag.entity.embed.TagEmbedModule;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.entity.embed.ViewerEmbed;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.entity.embed.ViewerEmbedModule;
import io.nicheblog.dreamdiary.global._common.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.model.CmmStus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ExptrPrsnlPaprEntity
 * <pre>
 *  경비관리 > 경비지출서 Entity
 *  ※ 경비지출서(ExptrPrsnlPapr) = 경비지출항목(ExptrPrsnlItem)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "exptr_prsnl_papr")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE exptr_prsnl_papr SET del_yn = 'Y' WHERE post_no = ?")
@NamedEntityGraph(
        name = "ExptrPrsnlPaprEntity.withCtgrCd",
        attributeNodes = {
                @NamedAttributeNode("ctgrCdInfo")
        }
)
public class ExptrPrsnlPaprEntity
        extends BasePostEntity
        implements CommentEmbedModule, TagEmbedModule, ManagtEmbedModule, ViewerEmbedModule {

    @PostLoad
    private void onLoad() {
        // 영수증 상태 세팅 :: 메소드 분리
        this.calcRciptStat();
    }

    /**
     * 영수증 상태 세팅 :: 메소드 분리
     */
    private void calcRciptStat() {
        if (CollectionUtils.isEmpty(this.itemList)) return;

        AtomicInteger itemCnt = new AtomicInteger();
        AtomicInteger totAmt = new AtomicInteger();
        AtomicInteger atchRciptCnt = new AtomicInteger();
        AtomicInteger orgnlRciptCnt = new AtomicInteger();
        AtomicInteger orgnlRciptNotNeededCnt = new AtomicInteger();

        itemList.stream()
                .filter(item -> !"Y".equals(item.getRjectYn())) // 거부된 아이템 제외
                .forEach(item -> {
                    itemCnt.incrementAndGet(); // 아이템 카운트 증가
                    totAmt.addAndGet(item.getExptrAmt()); // 총액에 아이템 금액 추가

                    if (item.getAtchFileDtlInfo() != null) atchRciptCnt.incrementAndGet(); // 첨부 파일이 있는 경우 카운트 증가
                    if ("Y".equals(item.getOrgnlRciptYn())) orgnlRciptCnt.incrementAndGet(); // 원본 영수증이 있는 경우 카운트 증가
                    if ("X".equals(item.getOrgnlRciptYn())) orgnlRciptNotNeededCnt.incrementAndGet(); // 원본 영수증이 필요 없는 경우 카운트 증가
                });

        this.itemCnt = itemCnt.get();
        this.totAmt = totAmt.get();
        this.atchRciptCnt = atchRciptCnt.get();
        this.orgnlRciptCnt = orgnlRciptCnt.get();
        this.orgnlRciptNotNeededCnt = orgnlRciptNotNeededCnt.get();

        // 영수증 첨부상태 세팅
        if (this.atchRciptCnt == 0) {
            this.setAtchRciptStus(new CmmStus("미제출", Constant.BS_DANGER, "bi bi-x"));
        } else if (this.atchRciptCnt < this.itemCnt) {
            this.setAtchRciptStus(new CmmStus("일부제출", Constant.BS_WARNING, "bi bi-caret-up"));
        } else if (this.atchRciptCnt.equals(this.itemCnt)) {
            this.setAtchRciptStus(new CmmStus("제출완료", Constant.BS_PRIMARY, "bi bi-circle fs-8"));
        }

        // 원본영수증 제출상태 세팅
        boolean hasExptr = this.itemCnt > 0;
        boolean allOrgnlRciptNotNeeded = hasExptr && Objects.equals(this.orgnlRciptNotNeededCnt, this.itemCnt);
        if (hasExptr && this.orgnlRciptCnt == 0 && !allOrgnlRciptNotNeeded) {
            this.setOrgnlRciptStus(new CmmStus("미제출", Constant.BS_DANGER, "bi bi-x"));
        } else if (this.orgnlRciptCnt + this.orgnlRciptNotNeededCnt < this.itemCnt) {
            this.setOrgnlRciptStus(new CmmStus("일부제출", Constant.BS_WARNING, "bi bi-caret-up"));
        } else if (this.orgnlRciptCnt + this.orgnlRciptNotNeededCnt == this.itemCnt) {
            this.setOrgnlRciptStus(new CmmStus("제출완료", Constant.BS_PRIMARY, "bi bi-circle fs-8"));
        }
    }

    /** 필수: 컨텐츠 타입 */
    @Builder.Default
    private static final ContentType CONTENT_TYPE = ContentType.EXPTR_PRSNL_PAPR;
    /** 필수(Override): 글분류 코드 */
    @Builder.Default
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 글 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("글 번호")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type", columnDefinition = "VARCHAR(50) DEFAULT 'EXPTR_PRSNL_PAPR'")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

    /** 글분류 코드 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'EXPTR_PRSNL_PAPR_CTGR_CD'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ctgr_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("경비지출서 글분류 코드 정보")
    private DtlCdEntity ctgrCdInfo;

    /* ----- */

    /** 년도 */
    @Column(name = "yy")
    @Comment("년도")
    private String yy;

    /** 월 */
    @Column(name = "mnth")
    @Comment("월")
    private Integer mnth;

    /** 확인 여부 (Y/N) */
    @Builder.Default
    @Column(name = "cf_yn")
    @Comment("확인 여부")
    private String cfYn = "N";

    /** 경비지출 항목 영수증 첨부파일 번호 */
    @Column(name = "rcipt_file_no")
    private Integer rciptFileNo;

    /** 경비지출 항목 목록 */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no")
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 10)
    @OrderBy("exptrDt ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("경비지출 항목 목록")
    private List<ExptrPrsnlItemEntity> itemList;

    /** 항목 건수 */
    @Transient
    @Builder.Default
    private Integer itemCnt = 0;

    /** 영수증 첨부 건수 */
    @Transient
    @Builder.Default
    private Integer atchRciptCnt = 0;

    /** 영수증 제출 건수 */
    @Transient
    @Builder.Default
    private Integer orgnlRciptCnt = 0;

    /** 영수증 불필요 건수 */
    @Transient
    @Builder.Default
    private Integer orgnlRciptNotNeededCnt = 0;

    /** 항목 건수 */
    @Transient
    @Builder.Default
    private Integer totAmt = 0;

    /** 영수증 스캔본 첨부 상태 */
    @Transient
    private CmmStus atchRciptStus;

    /** 영수증 원본 제출 상태 */
    @Transient
    private CmmStus orgnlRciptStus;

    /* ----- */

    /**
     * 서브엔티티 List 처리를 위한 Setter Override
     * 한 번 Entity가 생성된 이후부터는 새 List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     *
     * @param itemList - 설정할 객체 리스트
     */
    public void setItemList(final List<ExptrPrsnlItemEntity> itemList) {
        this.updtList(this.itemList, itemList);
    }

    /* ----- */

    /** 위임 :: 댓글 정보 모듈 */
    @Embedded
    public CommentEmbed comment;
    /** 위임 :: 태그 정보 모듈 */
    @Embedded
    public TagEmbed tag;
    /** 위임 :: 조치 정보 모듈 */
    @Embedded
    public ManagtEmbed managt;
    /** 위임 :: 열람 정보 모듈 */
    @Embedded
    public ViewerEmbed viewer;
}
