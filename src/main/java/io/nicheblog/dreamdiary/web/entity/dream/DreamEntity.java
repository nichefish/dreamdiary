package io.nicheblog.dreamdiary.web.entity.dream;

import io.nicheblog.dreamdiary.cmm.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.cmm.util.DateUtil;
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
 * DreamEntity
 * <pre>
 *  Entity that contains each distinct dream.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "DREAM")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE DREAM SET DEL_YN = 'Y' WHERE DREAM_NO = ?")
public class DreamEntity
        extends BaseCrudEntity
        implements Serializable {

    /**
     * 꿈 고유 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DREAM_NO")
    @Comment("꿈 고유 번호")
    private Integer dreamNo;

    /**
     * 꿈꾼 날짜
     */
    @Column(name = "DREAMT_DT")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtil.PTN_DATE)
    @Comment("꿈꾼 날짜")
    private Date dreamtDt;

    /**
     * 순번
     */
    @Column(name = "SORT_ORDR", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrdr;

    /**
     * 중요여부
     */
    @Builder.Default
    @Column(name = "IMPRTC_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("중요여부")
    private String imprtcYn = "N";

    /**
     * 꿈꾼이 (default: 나)
     */
    @Column(name = "DREAMER_NM", length = 64)
    private String dreamerNm;
}
