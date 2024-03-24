package io.nicheblog.dreamdiary.web.repository.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * DtlCdRepository
 * <pre>
 *  상세코드 repository 인터페이스
 *  ※상세코드(dtl_cd) = 분류코드 하위의 상세코드. 분류코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 */
@Repository("dtlCdRepository")
public interface DtlCdRepository
        extends BaseRepository<DtlCdEntity, DtlCdKey> {
    //
}
