package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * BaseAuditRegEntity
 * <pre>
 *  (공통/상속) Audit 정보 Entity (수정자 없이 등록자만)
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class BaseAuditRegEntity
        extends BaseCrudEntity {

    /** 등록자 ID */
    @CreatedBy
    @Column(name = "regstr_id", length = 20, updatable = false)
    protected String regstrId;

    /** 등록일시 */
    @CreatedDate
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "reg_dt", updatable = false)
    protected Date regDt;

    /** 등록자 정보 */
    @ManyToOne
    @JoinColumn(name = "regstr_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    protected AuditorInfo regstrInfo;
}

