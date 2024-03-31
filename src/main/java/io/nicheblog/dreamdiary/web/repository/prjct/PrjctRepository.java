package io.nicheblog.dreamdiary.web.repository.prjct;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.prjct.PrjctEntity;
import org.springframework.stereotype.Repository;

/**
 * PrjctRepository
 * <pre>
 *  프로젝트 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("prjctRepository")
public interface PrjctRepository
        extends BaseRepository<PrjctEntity, Integer> {
    //
}