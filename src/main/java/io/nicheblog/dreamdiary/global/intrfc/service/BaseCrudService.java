package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * BaseCrudService
 * <pre>
 *  (공통/상속) CRUD 공통 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseReadonlyService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
public interface BaseCrudService<Dto extends BaseCrudDto, ListDto extends BaseCrudDto, Key extends Serializable, Entity extends BaseCrudEntity, Repository extends BaseStreamRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseCrudMapstruct<Dto, ListDto, Entity>>
        extends BaseReadonlyService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    /**
     * default: 게시물 등록 전처리 (dto level)
     */
    default void preRegist(final Dto dto) throws Exception {
        // 등록 전처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 등록 후처리 (entity level, entity 변환 후 처리)
     */
    default void postRegist(final Entity rslt) throws Exception {
        // 등록 후처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 등록 (dto level)
     */
    default Dto regist(final Dto dto) throws Exception {
        // 등록 전처리
        this.preRegist(dto);

        // Dto -> Entity
        Mapstruct mapstruct = this.getMapstruct();
        Entity entity = mapstruct.toEntity(dto);

        // insert
        Entity rslt = this.updt(entity);

        // 등록 후처리
        this.postRegist(rslt);

        return mapstruct.toDto(rslt);
    }

    /**
     * default: 게시물 bulk-insert (entity level)
     */
    default boolean registAll(final List<Entity> entityList) {
        Repository repository = this.getRepository();
        repository.saveAllAndFlush(entityList);
        return true;
    }

    /**
     * default: 게시물 수정 전처리 (dto level)
     */
    default void preModify(final Dto dto) throws Exception {
        // 수정 전처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 중간처리 (entity level, entity 변환 후 처리)
     */
    default void midModify(final Entity entity) {
        // 수정 중간처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 후처리 (entity level, entity 변환 후 처리)
     */
    default void postModify(final Entity rslt) throws Exception {
        // 수정 후처리:: 기본 공백, 필요시 각 함수에서 Override
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

        // Entity 레벨 조회
        Mapstruct mapstruct = this.getMapstruct();
        Entity entity = this.getDtlEntity(key);
        mapstruct.updateFromDto(dto, entity);

        // 수정 중간처리
        this.midModify(entity);

        // update
        Repository repository = this.getRepository();
        Entity rslt = repository.saveAndFlush(entity);

        // 수정 후처리
        this.postModify(rslt);

        return mapstruct.toDto(rslt);
    }

    /**
     * default: 게시물 수정 (entity level)
     */
    @Transactional
    default Entity updt(final Entity e) {
        Repository repository = this.getRepository();
        Entity rslt = repository.saveAndFlush(e);
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

    /**
     * default: 게시물 bulk-delete (entity level)
     */
    default boolean deleteAll(List<Entity> entityList) {
        Repository repository = this.getRepository();
        repository.deleteAll(entityList);
        return true;
    }
    default boolean deleteAll(final Map<String, Object> searchParamMap) throws Exception {
        List<Entity> entityList = this.getListEntity(searchParamMap);
        Repository repository = this.getRepository();
        repository.deleteAll(entityList);
        return true;
    }
}
