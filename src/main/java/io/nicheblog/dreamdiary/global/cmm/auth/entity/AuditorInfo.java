package io.nicheblog.dreamdiary.global.cmm.auth.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.CmmDtlCdEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;

/**
 * AuditorInfo
 * <pre>
 *  (공통) Auditor(regstr, mdfusr) 정보 Entity
 *  연관관계 조회시에만 사용. 상호참조로 인한 무한재귀호출 방지를 위해서 UserEntity와 분리
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "USER")
@Getter
@Setter
@Where(clause = "DEL_YN='N'")
public class AuditorInfo
        implements Serializable {

    /**
     * 처리자 고유 ID (PK)
     */
    @Id
    @Column(name = "USER_NO", length = 20, insertable = false, updatable = false)
    private Integer userNo;

    /**
     * 처리자 정보 ID
     */
    @Column(name = "USER_INFO_NO", length = 20, insertable = false, updatable = false)
    private Integer userInfoNo;

    /**
     * 처리자 ID
     */
    @Column(name = "USER_ID", length = 20, insertable = false, updatable = false)
    private String userId;

    /**
     * 처리자 이름
     */
    @Column(name = "NICK_NM", length = 20, insertable = false, updatable = false)
    private String nickNm;

    /**
     * 권한코드
     */
    @Column(name = "AUTH_CD", length = 10)
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
     * 프로필 이미지 URL
     */
    @Column(name = "PROFL_IMG_URL", length = 1000)
    private String proflImgUrl;

    /* ----- */

    /**
     * getter override
     */
    public String getProflImgUrl() {
        if (StringUtils.isEmpty(this.proflImgUrl)) {
            return ("/metronic/assets/media/avatar_blank.png");
        }
        return this.proflImgUrl;
    }
}