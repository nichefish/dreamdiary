package io.nicheblog.dreamdiary.global.auth.entity;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseManageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * AuthRole
 * <pre>
 *  (공통) 권한 정보 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseManagtEntity
 */
@Entity
@Table(name = "auth_role")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
public class AuthRole
        extends BaseManageEntity {

    /**
     * 권한 코드 (PK)
     */
    @Id
    @Column(name = "auth_cd", length = 50)
    private String authCd;

    /**
     * 권한 이름
     */
    @Column(name = "auth_nm", length = 50)
    private String authNm;

    /**
     * 권한 레벨
     */
    @Column(name = "auth_level")
    private Integer authLevel;

    /**
     * 상위 권한 코드 (null일시 최상위 권한)
     */
    @Id
    @Column(name = "top_auth_cd", length = 50)
    private String topAuthCd;

    /**
     * 상위 권한 정보
     */
    @ManyToOne
    @JoinColumn(name = "top_auth_cd", referencedColumnName = "auth_cd", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("작업자 정보")
    private AuthRole topAuth;
}