package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.auth.model.AuditorDto;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * BaseAuditDto
 * <pre>
 *  (공통/상속) Audit 정보 Dto (수정자 정보 추가)
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditRegDto
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BaseAuditDto
        extends BaseAuditRegDto {

    /** 수정자 ID */
    protected String mdfusrId;
    /** 수정자 이름 */
    protected String mdfusrNm;
    /** 수정일시 */
    protected String mdfDt;

    /** 수정자 정보 */
    protected AuditorDto mdfusrInfo;

    /** 수정자 여부 */
    protected Boolean isMdfUser;
}
