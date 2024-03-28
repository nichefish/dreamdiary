package io.nicheblog.dreamdiary.web.repository.log;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.log.LogStatsUserEntity;
import io.nicheblog.dreamdiary.web.model.log.LogStatsUserIntrfc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
        extends BaseRepository<LogStatsUserEntity, String> {

    /** 로그인 유저별로 건수 조회 */
    @Query(
            value = "SELECT t.logUserId as userId, u.nickNm as userNm, count(t.logActvtyNo) as actvtyCnt " +
                    "FROM LogActvtyEntity t " +
                    "INNER JOIN UserEntity u ON t.logUserId = u.userId " +
                    "WHERE t.logDt between :searchStartDt and :searchEndDt and u.nickNm != null " +
                    "GROUP BY t.logUserId"
    )
    List<LogStatsUserIntrfc> getStatsUserIntrfcList(
            final Date searchStartDt,
            final Date searchEndDt
    );

    /** 비로그인 구분별로 건수 조회 */
    @Query(
            value = "SELECT t.logUserId as userId, u.nickNm as userNm, count(t.logActvtyNo) as actvtyCnt " +
                    "FROM LogActvtyEntity t " +
                    "LEFT JOIN UserEntity u ON t.logUserId = u.userId " +
                    "WHERE t.logDt between :searchStartDt and :searchEndDt and u.nickNm = null " +
                    "GROUP BY t.logUserId"
    )
    List<LogStatsUserIntrfc> getStatsNotUserIntrfcList(
            final Date searchStartDt,
            final Date searchEndDt
    );
}
