package io.nicheblog.dreamdiary.global.cmm.log.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

/**
 * LogActvtyEntity
 * 활동 로그 Entity
 */
@Entity
@Table(name = "log_actvty")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LogActvtyEntity
        implements Serializable {

    // 브라우저 정보 (브라우저 종류, 버전, 운영체제 등)
    // 장치 정보 (모바일 또는 데스크톱, 화면 해상도 등)
    // 클릭 이벤트 (링크, 버튼, 이미지 등)
    // 검색 쿼리 (검색에 특수 파라미터.. 붙이기..)
    // 동영상 재생 또는 음악 재생

    /**
     * 로그 고유 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_actvty_no")
    @Comment("로그 고유 ID (key)")
    private Integer logActvtyNo;

    /**
     * 작업자 ID
     */
    @CreatedBy
    @Column(name = "log_user_id", length = 20)
    @Comment("작업자 ID")
    protected String logUserId;

    /**
     * 작업자 정보
     */
    @ManyToOne
    @JoinColumn(name = "log_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    private AuditorInfo logUserInfo;

    /**
     * 작업일시
     */
    @CreatedDate
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "log_dt", updatable = false)
    @Comment("작업일시")
    private Date logDt;

    /**
     * 작업 구분 코드 (ex. 게시판, 공지사항, ...) (기능/모듈 단위)
     */
    @Column(name = "actvty_ctgr_cd", length = 400)
    @Comment("작업 구분 코드")
    private String actvtyCtgrCd;

    /**
     * 작업 구분 코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.ACTVTY_CTGR_CD + "\'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "actvty_ctgr_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("작업 구분 코드 정보")
    private DtlCdEntity actvtyCtgrInfo;

    /**
     * 작업 유형 코드 (조회, 검색, 처리...)
     */
    @Column(name = "action_ty_cd", length = 50)
    @Comment("작업 유형 코드")
    private String actionTyCd;

    /**
     * 작업 유형 코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.ACTION_TY_CD + "\'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "action_ty_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("작업 유형 코드 정보")
    private DtlCdEntity actionTyInfo;

    /**
     * 작업 URL
     */
    @Column(name = "url", length = 400)
    @Comment("작업 URL")
    private String url;

    /**
     * 작업 URL 정보
     */
    @OneToOne
    @JoinColumn(name = "url", referencedColumnName = "url", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("작업 URL 정보")
    private LogActvtyUrlNmEntity urlNmInfo;

    /**
     * 작업 파라미터
     */
    @Column(name = "param", length = 1000)
    @Comment("작업 파라미터")
    private String param;

    /**
     * 작업 내용
     */
    @Column(name = "cn", length = 400)
    @Comment("작업 내용")
    private String cn;

    /**
     * 리퍼러
     */
    @Column(name = "referer", length = 1000)
    @Comment("리퍼러")
    private String referer;

    /**
     * 작업자 IP
     */
    @Column(name = "ip_addr", length = 20)
    @Comment("작업자 IP")
    private String ipAddr;

    /**
     * 작업 결과
     */
    @Column(name = "rslt")
    @Comment("작업 결과")
    private Boolean rslt;

    /**
     * 작업 결과 메세지
     */
    @Column(name = "rslt_msg")
    @Comment("작업 결과 메세지")
    private String rsltMsg;

    /**
     * 익셉션 이름
     */
    @Column(name = "exception_nm")
    @Comment("익셉션 이름")
    private String exceptionNm;

    /**
     * 익셉션 메세지
     */
    @Column(name = "exception_msg")
    @Comment("익셉션 메세지")
    private String exceptionMsg;

    /* ----- */

    /**
     * 파라미터를 파라미터 맵으로 변환
     */
    public HashMap<String, String> getParamMap() {
        String paramStr = this.getParam();
        HashMap<String, String> paramMap = new HashMap<>();
        if (StringUtils.isEmpty(param)) return paramMap;

        String[] paramArray = param.split("&");
        for (String param : paramArray) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                if ("isMngr".equals(key)) continue;
                if ("isMngrMode".equals(key)) continue;
                if ("isDev".equals(key)) continue;
                paramMap.put(key, value);
            }
        }

        return paramMap;
    }
}
