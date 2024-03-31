package io.nicheblog.dreamdiary.global.cmm.file.service;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.mapstruct.AtchFileDtlMapstruct;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.file.repository.AtchFileDtlRepository;
import io.nicheblog.dreamdiary.global.cmm.file.spec.AtchFileDtlSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AtchFileDtlService
 * <pre>
 *  공통 > 상세 파일 처리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("atchFileDtlService")
@Log4j2
public class AtchFileDtlService
        implements BaseCrudService<AtchFileDtlDto, AtchFileDtlDto, Integer, AtchFileDtlEntity, AtchFileDtlRepository, AtchFileDtlSpec, AtchFileDtlMapstruct> {

    AtchFileDtlMapstruct atchFileDtlMapstruct = AtchFileDtlMapstruct.INSTANCE;

    @Resource(name = "atchFileDtlRepository")
    private AtchFileDtlRepository atchFileDtlRepository;
    @Resource(name = "atchFileDtlSpec")
    private AtchFileDtlSpec atchFileDtlSpec;

    @Override
    public AtchFileDtlRepository getRepository() {
        return this.atchFileDtlRepository;
    }
    @Override
    public AtchFileDtlSpec getSpec() {
        return this.atchFileDtlSpec;
    }
    @Override
    public AtchFileDtlMapstruct getMapstruct() {
        return this.atchFileDtlMapstruct;
    }

    /**
     * 첨부파일 상세 목록 조회 (dto level)
     */
    public List<AtchFileDtlDto> getListDto(final Integer atchFileNo) throws Exception {
        Map<String, Object> paramMap = new HashMap<>() {{
            put("atchFileNo", atchFileNo);
        }};
        return this.getListDto(paramMap, Pageable.unpaged()).getContent();
    }
}
