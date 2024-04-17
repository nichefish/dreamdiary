package io.nicheblog.dreamdiary.web.repository.jrnl.dream;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import org.springframework.stereotype.Repository;

/**
 * JrnlDreamRepository
 * <pre>
 *  저널 꿈 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlDreamRepository")
public interface JrnlDreamRepository
        extends BaseStreamRepository<JrnlDreamEntity, Integer> {

    //
}