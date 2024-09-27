package io.nicheblog.dreamdiary.web.service.notice;

import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import io.nicheblog.dreamdiary.web.mapstruct.notice.NoticeMapstruct;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeSearchParam;
import io.nicheblog.dreamdiary.web.model.notice.NoticeXlsxDto;
import io.nicheblog.dreamdiary.web.repository.notice.jpa.NoticeRepository;
import io.nicheblog.dreamdiary.web.spec.notice.NoticeSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * NoticeService
 * <pre>
 *  공지사항 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("noticeService")
@RequiredArgsConstructor
@Log4j2
public class NoticeService
        implements BasePostService<NoticeDto.DTL, NoticeDto.LIST, Integer, NoticeEntity, NoticeRepository, NoticeSpec, NoticeMapstruct> {

    private final NoticeRepository noticeRepository;
    private final NoticeSpec noticeSpec;
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

    /** 엑셀 다운로드 스트림 조회 */
    public Stream<NoticeXlsxDto> getStreamXlsxDto(NoticeSearchParam searchParam) throws Exception {
        Stream<NoticeEntity> entityStream = this.getStreamEntity(searchParam);
        return entityStream.map(e -> {
            try {
                return noticeMapstruct.toXlsxDto(e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}