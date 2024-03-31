package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.Serializable;

/**
 * BaseCrudService
 * <pre>
 *  (공통/상속) CRUD 공통 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 * @implements BaseReadonlyService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
public interface BaseCrudService<Dto extends BaseCrudDto, ListDto extends BaseCrudDto, Key extends Serializable, Entity extends BaseCrudEntity, Repository extends BaseRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseListMapstruct<Dto, ListDto, Entity>>
        extends BaseReadonlyService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    /**
     * default: 게시물 등록 전처리 (dto level)
     */
    default void preRegist(final Dto dto) throws Exception {
        // 등록 전처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 등록 (dto level)
     */
    default Dto regist(final Dto dto) throws Exception {
        // 등록 전처리
        this.preRegist(dto);

        Mapstruct mapstruct = this.getMapstruct();
        // Dto -> Entity
        Entity entity = mapstruct.toEntity(dto);
        // insert
        Entity rslt = this.updt(entity);
        return mapstruct.toDto(rslt);
    }

    /**
     * default: 게시물 수정 전처리 (dto level)
     */
    default void preModify(final Dto dto) throws Exception {
        // 수정 전처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 (dto level)
     */
    default Dto modify(
            final Dto dto,
            final Key key
    ) throws Exception {
        // 수정 전처리
        this.preModify(dto);

        Mapstruct mapstruct = this.getMapstruct();
        // Entity 레벨 조회
        Entity entity = this.getDtlEntity(key);
        mapstruct.updateFromDto(dto, entity);
        // update
        Repository repository = this.getRepository();
        Entity rsltEntity = repository.save(entity);
        return mapstruct.toDto(rsltEntity);
    }

    /**
     * default: 게시물 수정 (entity level)
     */
    @Transactional
    default Entity updt(final Entity e) {
        Repository repository = this.getRepository();
        Entity rslt = repository.save(e);
        try {
            repository.refresh(rslt);
        } catch (EntityNotFoundException ex) {
            ex.getStackTrace();
        }

        return rslt;
    }

    /**
     * default: 게시물 삭제 전처리
     */
    default void preDelete(final Entity e) {
        // 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 삭제
     */
    default Boolean delete(final Key key) throws Exception {
        Repository repository = this.getRepository();
        Entity e = this.getDtlEntity(key);
        if (e == null) return false;

        this.preDelete(e);

        repository.delete(e);
        return true;
    }
}
