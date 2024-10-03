package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstnModule;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * TmplatTxtDto
 * <pre>
 *  템플릿 텍스트 정보 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 * @implements StateCmpstnModule
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TmplatTxtDto
        extends BaseAtchDto
        implements Identifiable<Integer>, StateCmpstnModule {

    /** 고유 ID (PK) */
    @Positive
    private Integer tmplatTxtNo;

    /** 템플릿 정의 코드 */
    @Size(max = 50)
    private String tmplatDefCd;
    /** 템플릿 정의 정보 */
    // private TmplatDefDto tmplatDefInfo;

    /* ---- */

    /** 템플릿 구분 코드 (TEXTAREA vs. TEXTEDITOR) */
    @Size(max = 50)
    private String tmplatTyCd;

    /** 템플릿 분류(상세) 코드 */
    @Size(max = 50)
    private String ctgrCd;

    /** 제목 */
    private String title;

    /** 내용 (텍스트에디터 */
    private String cn;

    /** 기본설정 여부 (Y/N) */
    @Builder.Default
    @Size(min = 1, max = 1)
    @Pattern(regexp = "^[YN]$")
    private String defaultYn = "N";

    /* ----- */

    /** 상태 관리 모듈 (위임) */
    public StateCmpstn state;

    @Override
    public Integer getKey() {
        return this.tmplatTxtNo;
    }
}
