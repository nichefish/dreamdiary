package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstnModule;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * TmplatDefDto
 * <pre>
 *  템플릿 정의 정보 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 * @implements StateCmpstnModule {
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TmplatDefDto
        extends BaseAuditDto
        implements Identifiable<Integer>, StateCmpstnModule {

    /** 고유 ID (PK) */
    @Positive
    private Integer tmplatDefNo;

    /** 템플릿 정의 코드 */
    @Size(max = 50)
    private String tmplatDefCd;

    /** 제목 */
    private String title;

    /* ----- */

    /** 상태 관리 모듈 (위임) */
    public StateCmpstn state;

    @Override
    public Integer getKey() {
        return this.tmplatDefNo;
    }
}
