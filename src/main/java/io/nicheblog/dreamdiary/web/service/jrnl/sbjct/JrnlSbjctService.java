package io.nicheblog.dreamdiary.web.service.jrnl.sbjct;

import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import io.nicheblog.dreamdiary.web.entity.jrnl.sbjct.JrnlSbjctEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.sbjct.JrnlSbjctMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.sbjct.JrnlSbjctDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.sbjct.jpa.JrnlSbjctRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.sbjct.JrnlSbjctSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * JrnlSbjctService
 * <pre>
 *  공지사항 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlSbjctService")
@RequiredArgsConstructor
@Log4j2
public class JrnlSbjctService
        implements BasePostService<JrnlSbjctDto.DTL, JrnlSbjctDto.LIST, Integer, JrnlSbjctEntity, JrnlSbjctRepository, JrnlSbjctSpec, JrnlSbjctMapstruct> {

    private final JrnlSbjctRepository jrnlSbjctRepository;
    private final JrnlSbjctSpec jrnlSbjctSpec;
    private final JrnlSbjctMapstruct jrnlSbjctMapstruct = JrnlSbjctMapstruct.INSTANCE;

    @Override
    public JrnlSbjctRepository getRepository() {
        return this.jrnlSbjctRepository;
    }

    @Override
    public JrnlSbjctSpec getSpec() {
        return this.jrnlSbjctSpec;
    }

    @Override
    public JrnlSbjctMapstruct getMapstruct() {
        return this.jrnlSbjctMapstruct;
    }

    /** 엑셀 다운로드 스트림 조회 */
    // public Stream<JrnlSbjctXlsxDto> getStreamXlsxDto(JrnlSbjctSearchParam searchParam) throws Exception {
    //     Stream<JrnlSbjctEntity> entityStream = this.getStreamEntity(searchParam);
    //     return entityStream.map(e -> {
    //         try {
    //             return jrnlSbjctMapstruct.toXlsxDto(e);
    //         } catch (Exception ex) {
    //             throw new RuntimeException(ex);
    //         }
    //     });
    // }
}