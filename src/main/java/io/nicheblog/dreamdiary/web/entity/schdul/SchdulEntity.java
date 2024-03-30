package io.nicheblog.dreamdiary.web.entity.schdul;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

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
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE schdul SET del_yn = 'Y' WHERE schdul_no = ?")
public class SchdulEntity
        extends BaseClsfEntity {

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
    @Column(name = "content_type")
    private String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 일정 이름 */
    @Column(name = "title")
    @Comment("일정 이름")
    private String title;

    /** 내용 */
    @Column(name = "cn")
    @Comment("내용")
    private String cn;

    /** 일정 코드 */
    @Column(name = "schdul_cd")
    @Comment("일정분류코드")
    private String schdulCd;

    /** 시작일 */
    @Column(name = "bgn_dt")
    @Comment("시작일")
    private Date bgnDt;

    /** 일정 종료일 */
    @Column(name = "end_dt")
    @Comment("종료일")
    private Date endDt;

    /** 일정 비고 */
    @Column(name = "rm")
    @Comment("비고")
    private String rm;

    /** 개인일정 여부 (Y/N) */
    @Builder.Default
    @Column(name = "prvt_yn")
    @Comment("개인일정 여부 (Y/N)")
    private String prvtYn = "N";
}
