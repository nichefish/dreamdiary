package io.nicheblog.dreamdiary.web.repository.jrnl.sumry;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryCnEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JrnlSumryCnRepository
 * <pre>
 *  저널 결산 내용 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlSumryCnRepository")
public interface JrnlSumryCnRepository
        extends BaseStreamRepository<JrnlSumryCnEntity, Integer> {

    /**
     * 해당 결산에서 결산 내용 마지막 인덱스 조회
     */
    @Query("SELECT MAX(cn.idx) " +
            "FROM JrnlSumryCnEntity cn " +
            "INNER JOIN JrnlSumryEntity sumry ON cn.jrnlSumryNo = sumry.postNo " +
            "WHERE sumry.postNo = :jrnlSumryNo AND sumry.ctgrCd = :jrnlSumryTyCd")
    Optional<Integer> findLastIndexByJrnlSumryAndSumryTy(final @Param("jrnlSumryNo") Integer jrnlSumryNo, final @Param("jrnlSumryTyCd") String jrnlSumryTyCd);
}