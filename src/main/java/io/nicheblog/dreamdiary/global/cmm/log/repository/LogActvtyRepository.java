package io.nicheblog.dreamdiary.global.cmm.log.repository;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * LogActvtyRepository
 * <pre>
 *  활동 로그 관리 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("logActvtyRepository")
public interface LogActvtyRepository
        extends BaseRepository<LogActvtyEntity, Integer> {
    //
}
