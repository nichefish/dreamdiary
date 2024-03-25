package io.nicheblog.dreamdiary.web.repository.dream;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import org.springframework.stereotype.Repository;

/**
 * DreamDayRepository
 * <pre>
 *  꿈일자 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("dreamDayRepository")
public interface DreamDayRepository
        extends BaseRepository<DreamDayEntity, Integer> {

    //
}