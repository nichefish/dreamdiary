package io.nicheblog.dreamdiary.domain._core.cd.repository.jpa;

import io.nicheblog.dreamdiary.domain._core.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.domain._core.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * DtlCdRepository
 * <pre>
 *  상세 코드 repository 인터페이스
 *  ※상세 코드(dtl_cd) = 분류 코드 하위의 상세 코드. 분류 코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 */
@Repository("dtlCdRepository")
public interface DtlCdRepository
        extends BaseStreamRepository<DtlCdEntity, DtlCdKey> {
    //
}
