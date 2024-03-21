package io.nicheblog.dreamdiary.web.entity.schdul;

import io.nicheblog.dreamdiary.cmm.intrfc.entity.BaseCrudEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * SchdulEntity
 * 일정 Entity
 * (BaseAuditEntity 상속, Serializable 구현)
 *
 * @author nichefish
 */
@Entity
@Table(name = "SCHDUL")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE SCHDUL SET DEL_YN = 'Y' WHERE SCHDUL_NO = ?")
public class SchdulEntity
        extends BaseCrudEntity
        implements Serializable {

    /**
     * 일정 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHDUL_NO")
    @Comment("일정 번호")
    private Integer schdulNo;

    /**
     * 일정분류코드
     */
    @Column(name = "SCHDUL_TY_CD")
    @Comment("일정분류코드")
    private String schdulTyCd;

    /**
     * 일정분류명
     */
    @Column(name = "SCHDUL_NM")
    @Comment("일정(분류)명")
    private String schdulNm;

    /**
     * 일정 시작일
     */
    @Column(name = "BEGIN_DT")
    @Comment("일정 시작일")
    private Date beginDt;

    /**
     * 일정 종료일
     */
    @Column(name = "END_DT")
    @Comment("일정 종료일")
    private Date endDt;

    /**
     * 일정 사유
     */
    @Column(name = "SCHDUL_RESN")
    @Comment("일정 사유")
    private String schdulResn;

    /**
     * 일정 비고
     */
    @Column(name = "SCHDUL_RM")
    @Comment("일정 비고")
    private String schdulRm;
}
