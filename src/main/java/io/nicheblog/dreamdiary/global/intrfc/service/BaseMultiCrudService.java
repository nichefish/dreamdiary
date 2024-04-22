package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.cmm.file.utils.FileUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.transaction.Transactional;
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
public interface BaseMultiCrudService<Dto extends BaseAtchDto & Identifiable<Key>, ListDto extends BaseAtchDto, Key extends Serializable, Entity extends BaseAtchEntity, Repository extends BaseStreamRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseCrudMapstruct<Dto, ListDto, Entity>>
        extends BaseCrudService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    /**
     * default: 게시물 등록 (Multipart)
     */
    @Transactional
    default Dto regist(final Dto dto, final MultipartHttpServletRequest request) throws Exception {
        // 파일 영역 처리
        Integer atchFileNo = dto.getAtchFileNo();
        dto.setAtchFileNo(FileUtils.uploadFile(request, atchFileNo));    // 등록된 파일 마스터ID를 가져온다.
        // 나머지 처리
        return this.regist(dto);
    }

    /**
     * default: 게시물 수정 (Multipart)
     */
    @Transactional
    default Dto modify(final Dto dto, final MultipartHttpServletRequest request) throws Exception {
        // 파일 영역 처리
        Integer atchFileNo = dto.getAtchFileNo();
        dto.setAtchFileNo(FileUtils.uploadFile(request, atchFileNo));    // 등록된 파일 마스터ID를 가져온다.
        // 나머지 처리
        return this.modify(dto);
    }
}
