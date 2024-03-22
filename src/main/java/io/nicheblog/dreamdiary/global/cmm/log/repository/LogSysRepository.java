package io.nicheblog.dreamdiary.global.cmm.log.repository;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogSysEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * LogSysRepository
 * <pre>
 *  시스템 로그 관리 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("logSysRepository")
public interface LogSysRepository
        extends BaseRepository<LogSysEntity, Integer> {
    //
}
