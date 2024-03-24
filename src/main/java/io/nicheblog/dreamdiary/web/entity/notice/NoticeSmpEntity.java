package io.nicheblog.dreamdiary.web.entity.notice;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;

/**
 * NoticeSmpEntity
 * 공지사항 간소화 Entity
 * (NoticeEntity 태그 관련 정보 제거 = 연관관계 순환참조 방지 위함. 나머지는 동일)
 * (BasePostEntity 상속, Serializable 구현)
 *
 * @author nichefish
 */
@Entity
@Table(name = "NOTICE")
@IdClass(BaseClsfKey.class)      // 분류코드+상세코드 복합키 적용
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE NOTICE SET DEL_YN = 'Y' WHERE BOARD_CD = ? AND POST_NO = ?")
public class NoticeSmpEntity
        extends BasePostEntity
        implements Serializable {

    /**
     * 글 번호
     */
    @Id
    @TableGenerator(name = "notice", table = "CMM_SEQ", pkColumnName = "SEQ_NM", valueColumnName = "SEQ_VAL", pkColumnValue = "NOTICE_NO", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "notice")
    @Column(name = "POST_NO")
    @Comment("글 번호")
    private Integer postNo;

    /**
     * 글분류 코드
     */
    @Column(name = "CTGR_CD", length = 20)
    @Comment("글분류 코드")
    private String ctgrCd;

    /**
     * 공지사항 글분류 코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'NOTICE_CTGR_CD\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "CTGR_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("공지사항 글분류 코드 정보")
    private DtlCdEntity ctgrCdInfo;

    /**
     * 중요여부
     */
    @Builder.Default
    @Column(name = "IMPRTC_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("중요여부")
    private String imprtcYn = "N";

    /**
     * 상단고정여부
     */
    @Builder.Default
    @Column(name = "FXD_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("상단고정여부")
    private String fxdYn = "N";

    /**
     * 팝업 노출여부
     */
    @Builder.Default
    @Column(name = "POPUP_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("팝업 노출여부")
    private String popupYn = "N";

    /**
     * 수정권한
     */
    @Builder.Default
    @Column(name = "MDFABLE")
    @Comment("수정권한")
    private String mdfable = Constant.MDFABLE_REGSTR;
}
