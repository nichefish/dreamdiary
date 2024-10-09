package io.nicheblog.dreamdiary.domain.notice.service;

import io.nicheblog.dreamdiary.domain.notice.entity.NoticeEntity;
import io.nicheblog.dreamdiary.domain.notice.mapstruct.NoticeMapstruct;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeDto;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeSearchParam;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeXlsxDto;
import io.nicheblog.dreamdiary.domain.notice.repository.jpa.NoticeRepository;
import io.nicheblog.dreamdiary.domain.notice.spec.NoticeSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * NoticeService
 * <pre>
 *  공지사항 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService - 세부내용 변경시 해당 default 메소드 재정의(@Override)
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

    /**
     * 엑셀 다운로드 스트림 조회
     * @param searchParam - 검색 파라미터 객체
     * @return Stream<NoticeXlsxDto> - 변환된 NoticeXlsxDto 스트림
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
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