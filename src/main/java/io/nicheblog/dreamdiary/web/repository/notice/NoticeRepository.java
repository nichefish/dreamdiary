package io.nicheblog.dreamdiary.web.repository.notice;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import org.springframework.stereotype.Repository;

/**
 * NoticeRepository
 * <pre>
 *  공지사항 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("noticeRepository")
public interface NoticeRepository
        extends BaseStreamRepository<NoticeEntity, Integer> {

    //
}