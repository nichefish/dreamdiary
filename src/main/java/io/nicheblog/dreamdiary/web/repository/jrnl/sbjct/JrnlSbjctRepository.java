package io.nicheblog.dreamdiary.web.repository.jrnl.sbjct;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.sbjct.JrnlSbjctEntity;
import org.springframework.stereotype.Repository;

/**
 * JrnlSbjctRepository
 * <pre>
 *  일반게시판 게시물 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlSbjctRepository")
public interface JrnlSbjctRepository
        extends BaseStreamRepository<JrnlSbjctEntity, Integer> {
    //
}
