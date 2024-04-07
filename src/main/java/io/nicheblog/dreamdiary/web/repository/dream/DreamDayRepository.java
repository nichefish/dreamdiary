package io.nicheblog.dreamdiary.web.repository.dream;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
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
        extends BaseStreamRepository<DreamDayEntity, Integer> {

    //
}