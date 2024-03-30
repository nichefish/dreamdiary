package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * BaseAuditEntity
 * <pre>
 *  (공통/상속) Audit 정보 Entity (등록자 + 수정자)
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditRegEntity
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditEntity
        extends BaseAuditRegEntity {

    /** 수정자 ID */
    @LastModifiedBy
    @Column(name = "mdfusr_id", length = 20, insertable = false)
    protected String mdfusrId;

    /** 수정일시 */
    @LastModifiedDate
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "mdf_dt", insertable = false)
    protected Date mdfDt;

    /** 수정자 정보 */
    @ManyToOne
    @JoinColumn(name = "mdfusr_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    protected AuditorInfo mdfusrInfo;

    /** 수정자 이름 */
    @Transient
    protected String mdfusrNm;

    /** 수정자 여부 */
    @Transient
    protected Boolean isMdfusr;
}

