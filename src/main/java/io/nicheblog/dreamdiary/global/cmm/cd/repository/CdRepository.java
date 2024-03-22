package io.nicheblog.dreamdiary.global.cmm.cd.repository;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdKey;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CmmCdRepository
 * <pre>
 *  공통 코드 repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("cdRepository")
public interface CdRepository
        extends BaseRepository<DtlCdEntity, DtlCdKey> {

    /**
     * 분류코드로 상세코드 목록 검색
     */
    List<DtlCdEntity> findByClCd(
            final String clCd,
            final Sort sortOrdrAsc
    );

    /**
     * 분류코드로 상세코드 목록 검색
     */
    List<DtlCdEntity> findByClCdAndUseYn(
            final String clCd,
            String useYn,
            final Sort sortOrdrAsc
    );

    /**
     * 공통코드, 상세코드로 상세코드명 조회
     */
    DtlCdEntity findByClCdAndDtlCd(
            final String clCd,
            final String dtlCd
    );
}
