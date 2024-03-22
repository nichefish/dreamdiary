package io.nicheblog.dreamdiary.global.cmm.log.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * LogSysEntity
 * 시스템 로그 Entity
 */
@Entity
@Table(name = "LOG_SYS")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LogSysEntity
        implements Serializable {

    /**
     * 로그 고유 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_SYS_NO")
    @Comment("로그 고유 번호 (key)")
    private Integer logSysNo;

    /**
     * 로그유형코드 :
     * 활동(서비스)로그"ACTVTY" / 시스템로그"SYSTEM"
     * 별도 엔티티로 가는 게 맞는가??
     */
    @Builder.Default
    @Column(name = "LOG_TY_CD", length = 400)
    @Comment("로그유형코드")
    private String logTyCd = Constant.LOG_TY_SYS;

    /**
     * 작업자 ID
     */
    @Builder.Default
    @Column(name = "LOG_USER_ID", length = 20)
    @Comment("작업자 ID")
    protected String logUserId = Constant.SYSTEM_ACNT;

    /**
     * 작업자 정보
     */
    @ManyToOne
    @JoinColumn(name = "LOG_USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("작업자 정보")
    private AuditorInfo logUserInfo;

    /**
     * 작업일시
     */
    @CreatedDate
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "LOG_DT", updatable = false)
    @Comment("작업일시")
    private Date logDt;

    /**
     * 작업 구분 코드 (ex. 게시판, 공지사항, ...) (기능/모듈 단위)
     */
    @Column(name = "ACTVTY_CTGR_CD", length = 400)
    @Comment("작업 구분 코드")
    private String actvtyCtgrCd;

    /**
     * 작업 구분 코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.ACTVTY_CTGR_CD + "\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ACTVTY_CTGR_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("작업 구분 코드 정보")
    private DtlCdEntity actvtyCtgrInfo;

    /**
     * 작업 유형 코드 (조회, 검색, 처리...)
     */
    @Column(name = "ACTION_TY_CD", length = 50)
    @Comment("작업 유형 코드")
    private String actionTyCd;

    /**
     * 작업 유형 코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.ACTION_TY_CD + "\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ACTION_TY_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("작업 유형 코드 정보")
    private DtlCdEntity actionTyInfo;

    /**
     * 작업 URL
     */
    @Column(name = "URL", length = 400)
    @Comment("작업 URL")
    private String url;

    /**
     * 작업 내용
     */
    @Column(name = "CN", length = 400)
    @Comment("작업 내용")
    private String cn;

    /**
     * 작업 결과
     */
    @Column(name = "RSLT")
    @Comment("작업 결과")
    private Boolean rslt;

    /**
     * 작업 결과 메세지
     */
    @Column(name = "RSLT_MSG")
    @Comment("작업 결과 메세지")
    private String rsltMsg;

    /**
     * 익셉션 이름
     */
    @Column(name = "EXCEPTION_NM")
    @Comment("익셉션 이름")
    private String exceptionNm;

    /**
     * 익셉션 메세지
     */
    @Column(name = "EXCEPTION_MSG")
    @Comment("익셉션 메세지")
    private String exceptionMsg;

    /* ----- */
}
