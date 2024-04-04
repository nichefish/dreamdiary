package io.nicheblog.dreamdiary.api.dream.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;

/**
 * DreamPieceApiDto
 * <pre>
 *  꿈 일자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DreamPieceApiDto
        extends BasePostDto {

    /** 꿈 조각 고유 번호 (PK) */
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    private String contentType = "dream_piece";

    /** 꿈 일자 번호 */
    private Integer dreamDayNo;

    /** 순번 */
    private Integer sortOrdr;

    /** 내용 */
    private String cn;

    /** 편집완료 여부 (Y/N) */
    @Builder.Default
    private String editComptYn = "N";

    /** 타인 꿈 여부 (Y/N) */
    @Builder.Default
    private String elseDreamYn = "N";

    /** 꿈꾼이(타인) 이름 */
    @Column(name = "ELSE_DREAMER_NM", length = 64)
    private String elseDreamerNm;
}
