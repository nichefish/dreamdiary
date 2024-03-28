package io.nicheblog.dreamdiary.web.entity.flsys;

import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * FlsysMetaEntity
 * 파일시스템 메타정보 Entity
 * (BasePostEntity 상속, Serializable 구현)
 *
 * @author nichefish
 */
@Entity
@Table(name = "flsys_meta")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE flsys_meta SET del_yn = 'Y' WHERE content_type = ? AND post_no = ?")
public class FlsysMetaEntity
        extends BasePostEntity {

    /** 필수: 게시물 코드 */
    private static final String CONTENT_TYPE = "flsys_meta";
    /** 필수: 글분류 코드 */
    private static final String CTGR_CL_CD = "FLSYS_META_CTGR_CD";

    /**
     * 글 번호
     */
    @Id
    @TableGenerator(name = "flsys_meta", table = "cmm_sequence", pkColumnName = "seq_nm", valueColumnName = "seq_val", pkColumnValue = "flsys_meta_no", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "flsys_meta")
    @Column(name = "post_no")
    @Comment("글 번호")
    private Integer postNo;

    /**
     * 게시판 분류 코드
     */
    @Builder.Default
    @Column(name = "content_type")
    private String boardCd = CONTENT_TYPE;

    /**
     * 파일절대경로
     */
    @Column(name = "file_path", length = 500)
    @Comment("파일절대경로")
    private String filePath;

    /**
     * 상위파일절대경로
     */
    @Column(name = "upper_file_path", length = 500)
    @Comment("상위파일절대경로")
    private String upperFilePath;
}
