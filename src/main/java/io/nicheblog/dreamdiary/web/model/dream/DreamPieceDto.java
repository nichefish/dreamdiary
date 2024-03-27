package io.nicheblog.dreamdiary.web.model.dream;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * DreamPieceDto
 * <pre>
 *  꿈 일자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DreamPieceDto
        extends BaseClsfDto {

    /**
     * 꿈 조각 고유 번호 (PK)
     */
    private Integer postNo;

    /**
     * 꿈 일자 번호
     */
    private Integer dreamDayNo;

    /**
     * 순번
     */
    private Integer sortOrdr;

    /**
     * 내용
     */
    private String cn;

    /**
     * 편집완료여부
     */
    @Builder.Default
    private String editComptYn = "N";

    /**
     * 타인 꿈 여부
     */
    @Builder.Default
    private String elseDreamYn = "N";

    /**
     * 꿈꾼이(타인) 이름
     */
    @Column(name = "ELSE_DREAMER_NM", length = 64)
    private String elseDreamerNm;
}
