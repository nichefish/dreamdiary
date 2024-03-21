package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.CmmDtlCdEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * BaseUserEntity
 * <pre>
 *  (공통/상속) 사용자 정보 Entity:: 기본 구조 작동 위한 인터페이스
 *  ※"All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
public class BaseUserEntity
        extends BaseAtchEntity
        implements Serializable {

    /**
     * (로그인) 아이디
     */
    @Column(name = "USER_ID", length = 20, unique = true)
    private String userId;

    /**
     * 비밀번호
     * 암호화된 비밀번호(64bit)를 저장하기 위해 길이=64이다.
     */
    @Column(name = "USER_PW", length = 64)
    private String userPw;

    /**
     * 이름
     */
    @Column(name = "NICK_NM", length = 50)
    private String nickNm;

    /**
     * 권한코드
     */
    @Column(name = "AUTH_CD", length = 20)
    private String authCd;

    /**
     * 권한코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.AUTH_CD + "\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "AUTH_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    private CmmDtlCdEntity authCdInfo;

    /**
     * 잠금여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "LOCK_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String lockYn = "N";

    /**
     * 마지막 로그인 일시
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "LST_LGN_DT")
    private Date lstLgnDt;

    /**
     * 로그인 실패 횟수
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "LGN_FAIL_CNT", columnDefinition = "INT DEFAULT 0")
    private Integer lgnFailCnt = 0;

    /**
     * 패스워드 변경일시
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "PW_CHG_DT")
    private Date pwChgDt;

    /**
     * 계정 설명 (관리자용)
     */
    @Column(name = "USER_DC", length = 1000)
    private String userDc;

    /**
     * 패스워드 리셋 필요여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "NEEDS_PW_RESET", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String needsPwReset = "N";

    /**
     * 장기 미로그인 패스 체크 여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "DORMANT_BYPASS_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String dormantBypassYn = "N";
}