package io.nicheblog.dreamdiary.web.repository.admin;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.admin.PopupEntity;
import org.springframework.stereotype.Repository;

/**
 * PopupRepository
 * <pre>
 *  팝업 관리 Repository
 * </pre>
 *
 * @author nichefish
 */
@Repository("popupRepository")
public interface PopupRepository
        extends BaseStreamRepository<PopupEntity, Integer> {

    //
}
