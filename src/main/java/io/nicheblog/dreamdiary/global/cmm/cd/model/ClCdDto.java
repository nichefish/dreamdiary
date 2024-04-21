package io.nicheblog.dreamdiary.global.cmm.cd.model;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstnModule;
import io.nicheblog.dreamdiary.web.mapstruct.admin.DtlCdMapstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ClCdDto
 * <pre>
 *  분류코드(clCd) Dto
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 * @implements StateCmpstnModule
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClCdDto
        extends BaseAuditDto
        implements StateCmpstnModule, Identifiable<String> {

    /** 목록 순번 */
    private Long rnum;

    /** 분류코드 */
    private String clCd;
    /** 분류코드이름 */
    private String clCdNm;
    /** 분류코드설명 */
    private String dc;
    /** 분류 분류코드명 */
    private String clCtgrNm;

    /** 상세코드 목록 */
    List<DtlCdDto> dtlCdList;

    /** 상세코드 개수 */
    @Builder.Default
    private Integer dtlCdCnt = 0;

    /* --- */

    /**
     * 상세코드 entity 목록 반환
     */
    public List<DtlCdEntity> getDtlCdEntityList() throws Exception {
        if (CollectionUtils.isEmpty(this.dtlCdList)) return null;
        List<DtlCdEntity> dtlCdEntityList = new ArrayList<>();
        for (DtlCdDto dtlCd : this.dtlCdList) {
            DtlCdEntity entity = DtlCdMapstruct.INSTANCE.toEntity(dtlCd);
            dtlCdEntityList.add(entity);
        }
        return dtlCdEntityList;
    }

    /* ----- */

    /** 상태 관리 모듈 (위임) */
    public StateCmpstn state;

    @Override
    public String getKey() {
        return this.clCd;
    }
}
