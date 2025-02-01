package io.nicheblog.dreamdiary.global._common.file.service;

import io.nicheblog.dreamdiary.global._common.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global._common.file.mapstruct.AtchFileDtlMapstruct;
import io.nicheblog.dreamdiary.global._common.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global._common.file.repository.jpa.AtchFileDtlRepository;
import io.nicheblog.dreamdiary.global._common.file.spec.AtchFileDtlSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

/**
 * AtchFileDtlService
 * <pre>
 *  공통 > 상세 파일 처리 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
public interface AtchFileDtlService
        extends BaseCrudService<AtchFileDtlDto, AtchFileDtlDto, Integer, AtchFileDtlEntity, AtchFileDtlRepository, AtchFileDtlSpec, AtchFileDtlMapstruct> {

    /**
     * 첨부파일 상세 목록 조회 (dto level)
     *
     * @param atchFileNo 조회할 첨부파일 묶음 번호
     * @return {@link List} -- 첨부파일 상세 정보 목록
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    List<AtchFileDtlDto> getPageDto(final Integer atchFileNo) throws Exception;

    /**
     * 추가된 파일에 대하여 업로드 및 정보 DB에 등록한다.
     *
     * @param multiRequest 요청 정보
     * @param atchFileList 업로드된 파일 정보 목록
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    void addFiles(final MultipartHttpServletRequest multiRequest, final List<AtchFileDtlEntity> atchFileList) throws Exception;

    /**
     * 삭제된 파일에 대하여 DB 삭제 플래그를 세팅한다.
     *
     * @param multiRequest 요청 정보
     * @param atchFileList 업로드된 파일 정보 목록
     */
    void delFile(final MultipartHttpServletRequest multiRequest, final List<AtchFileDtlEntity> atchFileList);
}
