package io.nicheblog.dreamdiary.global.cmm.file.entity;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * AtchFileDtlEntity
 * <pre>
 *  첨부파일 상세 Entity
 *  ※첨부파일 상세(atch_file_dtl) = 실제 첨부파일 정보를 담고 있는 객체. 첨부파일(atch_file)에 N:1로 귀속된다.
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "ATCH_FILE_DTL")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE ATCH_FILE_DTL SET DEL_YN = 'Y' WHERE ATCH_FILE_DTL_NO = ?")
public class AtchFileDtlEntity
        extends BaseCrudEntity {

    /**
     * 첨부파일 상세 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATCH_FILE_DTL_NO")
    private Integer atchFileDtlNo;

    /**
     * 첨부파일 ID
     */
    @Column(name = "ATCH_FILE_NO")
    private Integer atchFileNo;

    /**
     * 파일 순번
     */
    @Column(name = "FILE_SN")
    private Integer fileSn;

    /**
     * 원본파일명
     */
    @Column(name = "ORGN_FILE_NM", length = 20)
    private String orgnFileNm;

    /**
     * 저장파일명
     */
    @Column(name = "STRE_FILE_NM", length = 20)
    private String streFileNm;

    /**
     * 파일 확장자
     */
    @Column(name = "FILE_EXTN", length = 20)
    private String fileExtn;

    /**
     * 파일 크기
     */
    @Column(name = "FILE_SIZE")
    private Long fileSize;

    /**
     * 파일 저장 경로
     */
    @Column(name = "FILE_STRE_PATH")
    private String fileStrePath;

    /**
     * URL (상대경로)
     */
    @Column(name = "URL")
    private String url;
}
