package io.nicheblog.dreamdiary.web.repository.jrnl.day;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import org.springframework.stereotype.Repository;

/**
 * JrnlDayRepository
 * <pre>
 *  꿈일자 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlDayRepository")
public interface JrnlDayRepository
        extends BaseStreamRepository<JrnlDayEntity, Integer> {

    //
}