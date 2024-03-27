package io.nicheblog.dreamdiary.global.auth.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
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
@Table(name = "user")
@Getter
@Setter
@Where(clause = "del_yn='N'")
public class AuditorInfo
        implements Serializable {

    /**
     * Auditor 고유 번호 (PK)
     */
    @Id
    @Column(name = "user_no", length = 20, insertable = false, updatable = false)
    private Integer userNo;

    /**
     * Auditor ID
     */
    @Column(name = "user_id", length = 20, insertable = false, updatable = false)
    private String userId;

    /**
     * 처리자 이름
     */
    @Column(name = "nick_nm", length = 20, insertable = false, updatable = false)
    private String nickNm;

    // TODO: 권한정보 조회

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profl_img_url", length = 1000)
    private String proflImgUrl;

    /* ----- */

    /**
     * getter override
     */
    public String getProflImgUrl() {
        if (StringUtils.isEmpty(this.proflImgUrl)) return ("/metronic/assets/media/avatar_blank.png");
        return this.proflImgUrl;
    }
}