package io.nicheblog.dreamdiary.web.service.notice;

import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import io.nicheblog.dreamdiary.web.mapstruct.notice.NoticeMapstruct;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeListDto;
import io.nicheblog.dreamdiary.web.repository.notice.NoticeRepository;
import io.nicheblog.dreamdiary.web.service.cmm.tag.TagService;
import io.nicheblog.dreamdiary.web.spec.notice.NoticeSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * NoticeService
 * <pre>
 *  공지사항 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseClsfService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("noticeService")
@Log4j2
public class NoticeService
        implements BasePostService<NoticeDto, NoticeListDto, Integer, NoticeEntity, NoticeRepository, NoticeSpec, NoticeMapstruct> {

    @Resource(name = "noticeRepository")
    private NoticeRepository noticeRepository;
    @Resource(name = "noticeSpec")
    private NoticeSpec noticeSpec;

    private final NoticeMapstruct noticeMapstruct = NoticeMapstruct.INSTANCE;

    @Override
    public NoticeRepository getRepository() {
        return this.noticeRepository;
    }

    @Override
    public NoticeSpec getSpec() {
        return this.noticeSpec;
    }

    @Override
    public NoticeMapstruct getMapstruct() {
        return this.noticeMapstruct;
    }
}