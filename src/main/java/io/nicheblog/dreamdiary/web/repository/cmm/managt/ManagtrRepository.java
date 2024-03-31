package io.nicheblog.dreamdiary.web.repository.cmm.managt;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.managt.ManagtrEntity;
import org.springframework.stereotype.Repository;

/**
 * ManagtrRepository
 * <pre>
 *  작업자 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("managtrRepository")
public interface ManagtrRepository
        extends BaseRepository<ManagtrEntity, Integer> {

    //
}