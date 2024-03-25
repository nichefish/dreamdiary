package io.nicheblog.dreamdiary.web.entity.dream;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * DreamPieceEntity
 * <pre>
 *  꿈 조각 Entity.
 *  Entity that contains each distinct dream.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "DREAM_PIECE")
@SuperBuilder(toBuilder=true)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE DREAM_PIECE SET DEL_YN = 'Y' WHERE DREAM_PIECE_NO = ?")
public class DreamPieceEntity
        extends BaseAtchEntity
        implements Serializable {

    /**
     * 꿈 조각 고유 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DREAM_PIECE_NO")
    @Comment("꿈 조각 고유 번호")
    private Integer dreamPieceNo;

    /**
     * 꿈 일자 번호
     */
    @Column(name = "DREAM_DAY_NO")
    @Comment("꿈 일자 번호")
    private Integer dreamDayNo;

    /**
     * 순번
     */
    @Column(name = "SORT_ORDR", columnDefinition = "INT DEFAULT 1")
    private Integer sortOrdr;

    /**
     * 내용
     */
    @Column(name = "CN")
    private String cn;

    /**
     * 중요여부
     */
    @Builder.Default
    @Column(name = "IMPRTC_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("중요여부")
    private String imprtcYn = "N";

    /**
     * 편집완료여부
     */
    @Builder.Default
    @Column(name = "EDIT_COMPT_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("편집완료여부")
    private String editComptYn = "N";

    /**
     * 타인 꿈 여부
     */
    @Builder.Default
    @Column(name = "ELSE_DREAM_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("타인 꿈 여부")
    private String elseDreamYn = "N";

    /**
     * 꿈꾼이(타인) 이름
     */
    @Column(name = "ELSE_DREAMER_NM", length = 64)
    private String elseDreamerNm;
}
