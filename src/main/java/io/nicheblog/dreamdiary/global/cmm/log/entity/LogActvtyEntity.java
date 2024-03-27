package io.nicheblog.dreamdiary.global.cmm.log.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
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
@Table(name = "LOG_ACTVTY")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
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
    @Column(name = "LOG_ACTVTY_NO")
    @Comment("로그 고유 ID (key)")
    private Integer logActvtyNo;

    /**
     * 작업자 ID
     */
    @CreatedBy
    @Column(name = "LOG_USER_ID", length = 20)
    @Comment("작업자 ID")
    protected String logUserId;

    /**
     * 작업자 정보
     */
    @ManyToOne
    @JoinColumn(name = "LOG_USER_ID", referencedColumnName = "user_id", insertable = false, updatable = false)
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
     * 작업 URL 정보
     */
    @OneToOne
    @JoinColumn(name = "URL", referencedColumnName = "URL", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("작업 URL 정보")
    private LogActvtyUrlNmEntity urlNmInfo;

    /**
     * 작업 파라미터
     */
    @Column(name = "PARAM", length = 1000)
    @Comment("작업 파라미터")
    private String param;

    /**
     * 작업 내용
     */
    @Column(name = "CN", length = 400)
    @Comment("작업 내용")
    private String cn;

    /**
     * 리퍼러
     */
    @Column(name = "REFERER", length = 1000)
    @Comment("리퍼러")
    private String referer;

    /**
     * 작업자 IP
     */
    @Column(name = "IP_ADDR", length = 20)
    @Comment("작업자 IP")
    private String ipAddr;

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
