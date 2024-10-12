package io.nicheblog.dreamdiary.domain.notice.service;

import io.nicheblog.dreamdiary.domain.notice.entity.NoticeEntity;
import io.nicheblog.dreamdiary.domain.notice.mapstruct.NoticeMapstruct;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeDto;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeSearchParam;
import io.nicheblog.dreamdiary.domain.notice.model.NoticeXlsxDto;
import io.nicheblog.dreamdiary.domain.notice.repository.jpa.NoticeRepository;
import io.nicheblog.dreamdiary.domain.notice.spec.NoticeSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import lombok.Getter;
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
 */
@Service("noticeService")
@RequiredArgsConstructor
@Log4j2
public class NoticeService
        implements BasePostService<NoticeDto.DTL, NoticeDto.LIST, Integer, NoticeEntity, NoticeRepository, NoticeSpec, NoticeMapstruct> {

    @Getter
    private final NoticeRepository repository;
    @Getter
    private final NoticeSpec spec;
    @Getter
    private final NoticeMapstruct mapstruct = NoticeMapstruct.INSTANCE;

    /**
     * 엑셀 다운로드 스트림 조회.
     *
     * @param searchParam 검색 파라미터 객체
     * @return {@link Stream} -- 변환된 Dto 스트림
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    public Stream<NoticeXlsxDto> getStreamXlsxDto(NoticeSearchParam searchParam) throws Exception {
        final Stream<NoticeEntity> entityStream = this.getStreamEntity(searchParam);

        return entityStream.map(e -> {
            try {
                return mapstruct.toXlsxDto(e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}