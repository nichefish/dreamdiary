package io.nicheblog.dreamdiary.global.cmm.file.entity;

import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AtchFileEntity
 * <pre>
 *  첨부파일 Entity
 *  ※첨부파일(atch_file) = 여러 첨부파일을 하나의 단위로 묶어놓은 객체. 첨부파일 상세(atch_file_dtl)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "ATCH_FILE")
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE ATCH_FILE SET DEL_YN = 'Y' WHERE ATCH_FILE_ID = ?")
public class AtchFileEntity
        implements Serializable {

    /**
     * 첨부파일 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATCH_FILE_ID", length = 20)
    private Integer atchFileId;

    /**
     * 첨부파일 상세 목록
     */
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "ATCH_FILE_ID")
    @Fetch(FetchMode.SELECT)
    @OrderBy("fileSn ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<AtchFileDtlEntity> atchFileList = new ArrayList<>();

    /**
     * 삭제여부
     */
    @Builder.Default
    @Column(name = "DEL_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String delYn = "N";

    /* ----- */

    /** 서브엔티티 List 처리를 위한 세터 */
    /**
     * 한 번 Entity가 생성된 이후부터는 새 List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     */
    public void setAtchFileList(final List<AtchFileDtlEntity> atchFileList) {
        if (CollectionUtils.isEmpty(atchFileList)) return;
        if (this.atchFileList == null) {
            this.atchFileList = atchFileList;
        } else {
            this.atchFileList.clear();
            this.atchFileList.addAll(atchFileList);
        }
    }
}
