package io.nicheblog.dreamdiary.global.cmm.cd.entity;

import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCd;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseManageEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.DtlCdMapstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ClCdEntity
 * <pre>
 *  분류코드(clCd) Entity
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseManageEntity
 */
@Entity
@Table(name = "CMM_CL_CD")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
public class ClCdEntity
        extends BaseManageEntity {

    /**
     * 분류코드
     */
    @Id
    @Column(name = "CL_CD")
    private String clCd;

    /**
     * 분류코드 이름
     */
    @Column(name = "CL_CD_NM")
    private String clCdNm;

    /**
     * 분류코드 설명
     */
    @Column(name = "CL_CD_DC")
    private String clCdDc;

    /**
     * 분류코드 정보
     */
    @OneToMany
    @JoinColumn(name = "CL_CD", referencedColumnName = "CL_CD", insertable = false, updatable = false)
    @OrderBy("sortOrdr ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @ToString.Exclude
    private List<DtlCdEntity> dtlCdList;

    /* ----- */

    /**
     * 상세코드 dto 목록 반환
     */
    public List<DtlCd> getDtlCdDtoList() throws Exception {
        if (CollectionUtils.isEmpty(this.dtlCdList)) return null;
        List<DtlCd> dtlCdDtoList = new ArrayList<>();
        long i = 1;
        for (DtlCdEntity dtlCdEntity : this.dtlCdList) {
            DtlCd dto = DtlCdMapstruct.INSTANCE.toDto(dtlCdEntity);
            dto.setRnum(i++);
            dtlCdDtoList.add(dto);
        }
        return dtlCdDtoList;
    }

    /** 서브엔티티 List 처리를 위한 세터 */
    /**
     * 한 번 Entity가 생성된 이후부터는 새 List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     */
    public void setDtlCdList(final List<DtlCdEntity> dtlCdList) {
        if (CollectionUtils.isEmpty(dtlCdList)) return;
        if (this.dtlCdList == null) {
            this.dtlCdList = dtlCdList;
        } else {
            this.dtlCdList.clear();
            this.dtlCdList.addAll(dtlCdList);
        }
    }
}
