package io.nicheblog.dreamdiary.web.repository.jrnl.summary;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.sumry.JrnlSumryEntity;
import org.springframework.stereotype.Repository;

/**
 * JrnlSumryRepository
 * <pre>
 *  저널 결산 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlSumryRepository")
public interface JrnlSumryRepository
        extends BaseStreamRepository<JrnlSumryEntity, Integer> {

    //
}