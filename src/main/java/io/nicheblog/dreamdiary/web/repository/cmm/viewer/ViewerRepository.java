package io.nicheblog.dreamdiary.web.repository.cmm.viewer;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.viewer.ViewerEntity;
import io.nicheblog.dreamdiary.web.entity.vcatn.stats.VcatnStatsYyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

/**
 * ViewerRepository
 * <pre>
 *  컨텐츠 열람자 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("viewerRepository")
public interface ViewerRepository
        extends BaseRepository<ViewerEntity, Integer> {

    //
}
