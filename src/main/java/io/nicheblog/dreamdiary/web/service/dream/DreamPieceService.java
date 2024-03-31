package io.nicheblog.dreamdiary.web.service.dream;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseClsfService;
import io.nicheblog.dreamdiary.web.entity.dream.DreamPieceEntity;
import io.nicheblog.dreamdiary.web.mapstruct.dream.DreamPieceMapstruct;
import io.nicheblog.dreamdiary.web.model.dream.piece.DreamPieceDto;
import io.nicheblog.dreamdiary.web.model.dream.piece.DreamPieceListDto;
import io.nicheblog.dreamdiary.web.repository.dream.DreamPieceRepository;
import io.nicheblog.dreamdiary.web.spec.dream.DreamPieceSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DreamPieceService
 * <pre>
 *  꿈 일자 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("dreamPieceService")
@Log4j2
public class DreamPieceService
        implements BaseClsfService<DreamPieceDto, DreamPieceListDto, Integer, DreamPieceEntity, DreamPieceRepository, DreamPieceSpec, DreamPieceMapstruct> {

    private final DreamPieceMapstruct dreamPieceMapstruct = DreamPieceMapstruct.INSTANCE;

    @Resource(name = "dreamPieceRepository")
    private DreamPieceRepository dreamPieceRepository;
    @Resource(name = "dreamPieceSpec")
    private DreamPieceSpec dreamPieceSpec;

    @Override
    public DreamPieceRepository getRepository() {
        return this.dreamPieceRepository;
    }

    @Override
    public DreamPieceMapstruct getMapstruct() {
        return this.dreamPieceMapstruct;
    }

    @Override
    public DreamPieceSpec getSpec() {
        return this.dreamPieceSpec;
    }
}