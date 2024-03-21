package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.cmm.auth.model.AuditorDto;
import io.nicheblog.dreamdiary.global.cmm.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.util.MaskingUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

/**
 * BaseAuditRegDto
 * <pre>
 *  (공통/상속) Audit 정보 Dto (등록자 정보 추가)
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudDto
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@EqualsAndHashCode(callSuper = false)
public class BaseAuditRegDto
        extends BaseCrudDto {

    /**
     * 등록자 ID
     */
    protected String regstrId;
    /**
     * 등록자 이름
     */
    protected String regstrNm;
    /**
     * 등록일시
     */
    protected String regDt;

    /**
     * 등록자 정보
     */
    private AuditorDto regstrInfo;

    /* ----- */

    /**
     * 처리성공여부 = 서비스 레벨에서 결과값 반환시 사용
     */
    protected Boolean isSuccess;

    /**
     * 등록자 여부
     */
    public Boolean getIsRegstr() {
        return (AuthUtils.isRegstr(this.regstrId));
    }

    /**
     * 마스킹 처리한 사용자ID 반환
     */
    public String getMaskedRegstrId() throws Exception {
        return MaskingUtils.nameMasking(this.getRegstrId());
    }
}
