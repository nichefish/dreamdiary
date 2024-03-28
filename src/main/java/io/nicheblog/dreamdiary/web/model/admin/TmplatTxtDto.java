package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * TmplatTxtDto
 * <pre>
 *  템플릿 정보 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TmplatTxtDto
        extends BaseAtchDto {

    /**
     * 고유 ID (PK)
     */
    private Integer tmplatTxtNo;

    /**
     * 템플릿 정의 코드
     */
    private String tmplatDefCd;
    /** 템플릿 정의 정보 */
    // private TmplatDefDto tmplatDefInfo;

    /* ---- */

    /**
     * 템플릿 구분 코드 (TEXTAREA vs. TEXTEDITOR)
     */
    private String tmplatTyCd;
    /**
     * 템플릿 분류(상세) 코드
     */
    private String ctgrCd;

    /**
     * 제목
     */
    private String title;
    /**
     * 내용 (텍스트에디터
     */
    private String cn;
    /**
     * 기본설정 여부
     */
    @Builder.Default
    private String defaultYn = "N";
    /**
     * 정렬 순서
     */
    private Integer sortOrdr;
    /**
     * 사용여부
     */
    @Builder.Default
    private String useYn = "N";
}
