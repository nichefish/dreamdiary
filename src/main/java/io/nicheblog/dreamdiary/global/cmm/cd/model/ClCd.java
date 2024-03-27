package io.nicheblog.dreamdiary.global.cmm.cd.model;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.mapstruct.CdMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.web.mapstruct.admin.ClCdMapstruct;
import io.nicheblog.dreamdiary.web.mapstruct.admin.DtlCdMapstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.checkerframework.checker.units.qual.N;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ClCd
 * <pre>
 *  분류코드(clCd) Dto
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class ClCd
        extends BaseAuditDto {

    /**
     * 목록 순번
     */
    private Long rnum;

    /**
     * 분류코드
     */
    private String clCd;
    /**
     * 분류코드이름
     */
    private String clCdNm;
    /**
     * 분류코드설명
     */
    private String clCdDc;
    /**
     * 사용 여부
     */
    private String useYn;

    /**
     * 상세코드 목록
     */
    List<DtlCd> dtlCdList;

    /**
     * 성공여부
     */
    private Boolean isSuccess;

    /* --- */

    /**
     * 상세코드 개수 반환
     */
    public Integer getDtlCdCnt() {
        if (CollectionUtils.isEmpty(this.dtlCdList)) return 0;
        return this.dtlCdList.size();
    }

    /**
     * 상세코드 entity 목록 반환
     */
    public List<DtlCdEntity> getDtlCdEntityList() throws Exception {
        if (CollectionUtils.isEmpty(this.dtlCdList)) return null;
        List<DtlCdEntity> dtlCdEntityList = new ArrayList<>();
        for (DtlCd dtlCd : this.dtlCdList) {
            DtlCdEntity entity = DtlCdMapstruct.INSTANCE.toEntity(dtlCd);
            dtlCdEntityList.add(entity);
        }
        return dtlCdEntityList;
    }
}
