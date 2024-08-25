package io.nicheblog.dreamdiary.web.repository.cmm.viewer.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.viewer.ViewerEntity;
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
        extends BaseStreamRepository<ViewerEntity, Integer> {

    //
}
