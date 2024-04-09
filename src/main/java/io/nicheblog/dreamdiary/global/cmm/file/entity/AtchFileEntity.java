package io.nicheblog.dreamdiary.global.cmm.file.entity;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AtchFileEntity
 * <pre>
 *  첨부파일 Entity
 *  첨부파일(atch_file) = 여러 첨부파일을 하나의 단위로 묶어놓은 객체. 첨부파일 상세(atch_file_dtl)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "atch_file")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE atch_file SET del_yn = 'Y' WHERE atch_file_no = ?")
public class AtchFileEntity
        extends BaseCrudEntity {

    /** 첨부파일 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atch_file_no", length = 20)
    private Integer atchFileNo;

    /** 첨부파일 상세 목록 */
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "atch_file_no")
    @Fetch(FetchMode.SELECT)
    @OrderBy("fileSn ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<AtchFileDtlEntity> atchFileList = new ArrayList<>();

    /* ----- */

    /**
     * 서브엔티티 List 처리를 위한 세터
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

    /** cascade 처리 위한 임시조치 */
    public void cascade() {
        Optional.ofNullable(this.atchFileList)
                .ifPresent(list -> list.forEach(dtlFile -> dtlFile.setAtchFileInfo(this)));
    }

}
