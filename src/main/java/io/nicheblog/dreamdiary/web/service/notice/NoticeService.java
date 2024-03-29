package io.nicheblog.dreamdiary.web.service.notice;

import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.file.service.FileService;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseClsfService;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import io.nicheblog.dreamdiary.web.mapstruct.notice.NoticeMapstruct;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeListDto;
import io.nicheblog.dreamdiary.web.repository.notice.NoticeRepository;
import io.nicheblog.dreamdiary.web.spec.notice.NoticeSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        implements BaseClsfService<NoticeDto, NoticeListDto, Integer, NoticeEntity, NoticeRepository, NoticeSpec, NoticeMapstruct, FileService> {

    @Resource(name = "noticeRepository")
    private NoticeRepository noticeRepository;
    @Resource(name = "noticeSpec")
    private NoticeSpec noticeSpec;
    @Resource(name = "fileService")
    private FileService fileService;

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

    @Override
    public FileService getFileService() {
        return this.fileService;
    }

    // @Resource(name = "boardTagService")
    // private BoardTagService boardTagService;
    @Resource(name = "cdService")
    public CdService cdService;

    /**
     * 공지사항 > 공지사항 상단 고정 목록 조회
     */
    public List<NoticeListDto> getFxdList() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("fxdYn", "Y");
        }};

        Page<NoticeEntity> entityList = this.getListEntity(searchParamMap, Pageable.unpaged());
        List<NoticeListDto> dtoList = new ArrayList<>();
        for (NoticeEntity entity : entityList.getContent()) {
            dtoList.add(noticeMapstruct.toListDto(entity));
        }
        return dtoList;
    }

    /**
     * 공지사항 > 공지사항 등록 전처리
     */
    @Override
    public void preRegist(final NoticeDto noticeDto) throws Exception {
        // 태그를 먼저 처리해준다. :: 메소드 분리
        // boardTagService.processTagList(noticeDto);
    }

    /**
     * 공지사항 > 공지사항 수정 전처리
     */
    @Override
    public void preModify(final NoticeDto noticeDto) throws Exception {
        // 태그를 먼저 처리해준다. :: 메소드 분리
        // boardTagService.processTagList(noticeDto);
    }

}