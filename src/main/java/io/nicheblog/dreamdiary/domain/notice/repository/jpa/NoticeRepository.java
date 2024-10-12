package io.nicheblog.dreamdiary.domain.notice.repository.jpa;

import io.nicheblog.dreamdiary.domain.notice.entity.NoticeEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * NoticeRepository
 * <pre>
 *  공지사항 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("noticeRepository")
public interface NoticeRepository
        extends BaseStreamRepository<NoticeEntity, Integer> {
    //
}