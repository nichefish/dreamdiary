package io.nicheblog.dreamdiary.web.repository.cmm.sectn;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.sectn.SectnEntity;
import org.springframework.stereotype.Repository;

/**
 * SectnRepository
 * <pre>
 *  단락 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("sectnRepository")
public interface SectnRepository
        extends BaseStreamRepository<SectnEntity, Integer> {
    //
}