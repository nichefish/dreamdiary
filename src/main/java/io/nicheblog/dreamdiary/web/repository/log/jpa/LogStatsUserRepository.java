package io.nicheblog.dreamdiary.web.repository.log.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.log.LogStatsUserEntity;
import io.nicheblog.dreamdiary.web.model.log.LogStatsUserIntrfc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * LogStatsUserRepository
 * <pre>
 *  활동 로그 사용자별 통계 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("logStatsUserRepository")
public interface LogStatsUserRepository
        extends BaseStreamRepository<LogStatsUserEntity, String> {

    /** 로그인 유저별로 건수 조회 */
    @QueryHints(value=@QueryHint(name="org.hibernate.readOnly", value="true"))
    @Query(
            value = "SELECT t.userId as userId, u.nickNm as userNm, count(t.logActvtyNo) as actvtyCnt " +
                    "FROM LogActvtyEntity t " +
                    "INNER JOIN FETCH UserEntity u ON t.userId = u.userId " +
                    "WHERE t.logDt between :searchStartDt and :searchEndDt and u.nickNm != null " +
                    "GROUP BY t.userId"
    )
    List<LogStatsUserIntrfc> getStatsUserIntrfcList(
            final @Param("searchStartDt") Date searchStartDt,
            final @Param("searchEndDt") Date searchEndDt
    );

    /** 비로그인 구분별로 건수 조회 */
    @QueryHints(value=@QueryHint(name="org.hibernate.readOnly", value="true"))
    @Query(
            value = "SELECT t.userId as userId, u.nickNm as userNm, count(t.logActvtyNo) as actvtyCnt " +
                    "FROM LogActvtyEntity t " +
                    "LEFT JOIN FETCH UserEntity u ON t.userId = u.userId " +
                    "WHERE t.logDt between :searchStartDt and :searchEndDt and u.nickNm = null " +
                    "GROUP BY t.userId"
    )
    List<LogStatsUserIntrfc> getStatsNotUserIntrfcList(
            final @Param("searchStartDt") Date searchStartDt,
            final @Param("searchEndDt") Date searchEndDt
    );
}
