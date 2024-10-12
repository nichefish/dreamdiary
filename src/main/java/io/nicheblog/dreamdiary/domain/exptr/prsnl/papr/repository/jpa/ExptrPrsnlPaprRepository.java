package io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.repository.jpa;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * ExptrPrsnlRepository
 * <pre>
 *  경비 관리 > 경비지출서 > 경비지출서 (JPA) Repository 인터페이스.
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Repository("exptrPrsnlPaprRepository")
public interface ExptrPrsnlPaprRepository
        extends BaseStreamRepository<ExptrPrsnlPaprEntity, Integer> {

    /** entityGraph */
    @Override
    @EntityGraph(value = "ExptrPrsnlPaprEntity.withCtgrCd")
    Optional<ExptrPrsnlPaprEntity> findById(final Integer key);

    /** entityGraph */
    @Override
    @EntityGraph(value = "ExptrPrsnlPaprEntity.withCtgrCd")
    Page<ExptrPrsnlPaprEntity> findAll(final @Nullable Specification<ExptrPrsnlPaprEntity> spec, final Pageable pageable);

    /** entityGraph */
    @Override
    @EntityGraph(value = "ExptrPrsnlPaprEntity.withCtgrCd")
    List<ExptrPrsnlPaprEntity> findAll(final @Nullable Specification<ExptrPrsnlPaprEntity> spec);

    /** entityGraph */
    @Override
    @EntityGraph(value = "ExptrPrsnlPaprEntity.withCtgrCd")
    List<ExptrPrsnlPaprEntity> findAll(final @Nullable Specification<ExptrPrsnlPaprEntity> spec, final Sort sort);

    /** entityGraph */
    @Override
    @EntityGraph(value = "ExptrPrsnlPaprEntity.withCtgrCd")
    Stream<ExptrPrsnlPaprEntity> streamAllBy(final @Nullable Specification<ExptrPrsnlPaprEntity> spec);

    /**
     * 존재하는 경비지출서 중 최저 년도 조회
     *
     * @return {@link String} -- 최저 년도 (String)
     */
    @Transactional(readOnly = true)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT min(t.yy) " +
            "FROM ExptrPrsnlPaprEntity t")
    String selectMinYy();

    /**
     * 경비지출서 항목 첨부파일 번호 변경
     *
     * @param exptrPrsnlItemNo 경비지출서 항목 번호
     * @param atchFileDtlNo 첨부파일 상세 번호
     * @return {@link Integer} -- 업데이트된 행의 수
     */
    @Modifying
    @Query("UPDATE ExptrPrsnlItemEntity t " +
            "SET t.atchFileDtlNo = :atchFileDtlNo " +
            "WHERE t.exptrPrsnlItemNo = :exptrPrsnlItemNo")
    Integer updateRciptFile(
            final @Param("exptrPrsnlItemNo") Integer exptrPrsnlItemNo,
            final @Param("atchFileDtlNo") Integer atchFileDtlNo
    );
}