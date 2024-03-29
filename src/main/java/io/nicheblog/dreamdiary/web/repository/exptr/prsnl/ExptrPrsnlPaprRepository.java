package io.nicheblog.dreamdiary.web.repository.exptr.prsnl;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlPaprEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * ExptrPrsnlRepository
 * <pre>
 *  경비 관리 > 경비지출서 > 경비지출서 Repository 인터페이스
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Repository("exptrPrsnlPaprRepository")
public interface ExptrPrsnlPaprRepository
        extends BaseRepository<ExptrPrsnlPaprEntity, Integer> {

    /**
     * 존재하는 경비지출서 중 최저년도 조회
     */
    @Query(
            value = "SELECT min(t.yy) " +
                    "FROM ExptrPrsnlPaprEntity t"
    )
    String selectMinYy();

    /**
     * userInfoNo, yy, mnth로 sum 구하기
     */
    // @Query(
    //         value = "SELECT sum(tt.exptrAmt) " +
    //                 "FROM ExptrPrsnlPaprEntity t " +
    //                 "inner join ExptrPrsnlItemEntity tt on t.postNo = tt.refPostNo " +
    //                 "inner join UserEntity u on t.regstrId = u.userId " +
    //                 "where t.yy = :yyStr and t.mnth = :mnth"
    // )
    // Optional<Integer> selectSum(
    //         final @Param("userProflNo") Integer userProflNo,
    //         String yyStr,
    //         String mnth
    // );

    /**
     * 경비지출서 항목 첨부파일 번호 변경
     */
    @Query(
            value = "UPDATE exptr_prsnl_item " +
                    "SET atch_file_dtl_no = :atchFileDtlNo " +
                    "WHERE exptr_prsnl_item_no = :exptrPrsnlItemNo",
            nativeQuery = true
    )
    Integer updateExptrPrsnlItemRcipt(
            final @Param("exptrPrsnlItemNo") Integer exptrPrsnlItemNo,
            final @Param("atchFileDtlNo") Integer atchFileDtlNo
    );
}