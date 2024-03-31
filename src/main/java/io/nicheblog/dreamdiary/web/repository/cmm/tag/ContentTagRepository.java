package io.nicheblog.dreamdiary.web.repository.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import org.springframework.stereotype.Repository;

/**
 * ContentTagRepository
 * <pre>
 *  컨텐츠 태그 정보 repository 인터페이스
 *  (cascade하지 않고 수동 관리)
 * </pre>
 *
 * @author nichefish
 */

@Repository("contentTagRepository")
public interface ContentTagRepository
        extends BaseRepository<ContentTagEntity, Integer> {
    //
}
