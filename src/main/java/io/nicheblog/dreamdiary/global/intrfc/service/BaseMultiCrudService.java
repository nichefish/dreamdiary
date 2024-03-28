package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.Serializable;

/**
 * BaseMultiCrudInterface
 * <pre>
 *  (공통/상속) MultipartRequest(파일업로드)를 사용하는 CRUD 공통 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
public interface BaseMultiCrudService<Dto extends BaseAtchDto, ListDto extends BaseAtchDto, Key extends Serializable, Entity extends BaseAtchEntity, Repository extends BaseRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseListMapstruct<Dto, ListDto, Entity>, FileService extends io.nicheblog.dreamdiary.global.cmm.file.service.FileService>
        extends BaseCrudService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    // Resource : repository
    io.nicheblog.dreamdiary.global.cmm.file.service.FileService getFileService();

    /**
     * default: 게시물 등록 (Multipart)
     */
    default Dto regist(
            final Dto dto,
            final MultipartHttpServletRequest request
    ) throws Exception {
        io.nicheblog.dreamdiary.global.cmm.file.service.FileService fileService = this.getFileService();
        // 파일 영역 처리
        Integer atchFileNo = dto.getAtchFileNo();
        dto.setAtchFileNo(fileService.uploadFile(request, atchFileNo));    // 등록된 파일 마스터ID를 가져온다.
        // 나머지 처리
        return this.regist(dto);
    }

    /**
     * default: 게시물 수정 (Multipart)
     */
    default Dto modify(
            final Dto dto,
            final Key key,
            final MultipartHttpServletRequest request
    ) throws Exception {
        io.nicheblog.dreamdiary.global.cmm.file.service.FileService fileService = this.getFileService();
        // 파일 영역 처리
        Integer atchFileNo = dto.getAtchFileNo();
        dto.setAtchFileNo(fileService.uploadFile(request, atchFileNo));    // 등록된 파일 마스터ID를 가져온다.
        // 나머지 처리
        return this.modify(dto, key);
    }
}
