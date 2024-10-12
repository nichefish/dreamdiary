package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
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
 *  (공통/상속) CRUD 공통 서비스 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
public interface BaseCrudService<Dto extends BaseCrudDto & Identifiable<Key>, ListDto extends BaseCrudDto, Key extends Serializable, Entity extends BaseCrudEntity, Repository extends BaseStreamRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseCrudMapstruct<Dto, ListDto, Entity>>
        extends BaseReadonlyService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    /**
     * default: 게시물 등록 전처리 (dto level)
     *
     * @param registDto 등록할 DTO 객체
     * @throws Exception 전처리 중 발생할 수 있는 예외
     */
    default void preRegist(final Dto registDto) throws Exception {
        // 등록 전처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 등록 중간처리
     *
     * @param registEntity 등록 전 entity 객체
     * @throws Exception 후처리 중 발생할 수 있는 예외
     */
    default void midRegist(final Entity registEntity) throws Exception {
        // 등록 중간처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 등록 후처리 (entity level, entity 변환 후 처리)
     *
     * @param updatedEntity - 등록된 엔티티 객체
     * @throws Exception 후처리 중 발생할 수 있는 예외
     */
    default void postRegist(final Entity updatedEntity) throws Exception {
        // 등록 후처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 등록 (dto level)
     *
     * @param registDto 등록할 DTO 객체
     * @return {@link Dto} -- 등록 결과를 DTO로 변환한 객체
     * @throws Exception 등록 중 발생할 수 있는 예외
     */
    @Transactional
    default Dto regist(final Dto registDto) throws Exception {
        // 등록 전처리
        this.preRegist(registDto);

        // Dto -> Entity 변환
        final Mapstruct mapstruct = this.getMapstruct();
        final Entity registEntity = mapstruct.toEntity(registDto);

        // 등록 중간처리
        this.midRegist(registEntity);

        // insert
        final Entity updatedEntity = this.updt(registEntity);

        // 등록 후처리
        this.postRegist(updatedEntity);

        return mapstruct.toDto(updatedEntity);
    }

    /**
     * default: 게시물 bulk-insert (entity level)
     *
     * @param entityList - 등록할 엔티티 리스트
     * @return {@link Boolean} -- 등록 성공시 true
     */
    @Transactional
    default boolean registAll(final List<Entity> entityList) {
        final Repository repository = this.getRepository();
        final List<Entity> updatedEntityList = repository.saveAllAndFlush(entityList);

        // 벌크 등록 후처리
        this.postRegistAll(updatedEntityList);
        
        return true;
    }

    /**
     * default: 게시물 bulk 등록 후처리
     *
     * @param updatedEntityList - 등록된 엔티티 리스트
     */
    default void postRegistAll(final List<Entity> updatedEntityList) {
        // 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 전처리 (dto level)
     *
     * @param modifyDto 수정할 DTO 객체
     * @throws Exception 수정 전처리 중 발생할 수 있는 예외
     */
    default void preModify(final Dto modifyDto) throws Exception {
        // 수정 전처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 중간처리 (entity level, entity 변환 후 처리)
     *
     * @param modifyEntity 수정 중간처리를 할 엔티티 객체
     */
    default void midModify(final Entity modifyEntity) {
        // 수정 중간처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 후처리 (entity level, entity 변환 후 처리)
     *
     * @param updatedEntity - 수정된 엔티티 객체
     * @throws Exception 후처리 중 발생할 수 있는 예외
     */
    default void postModify(final Entity updatedEntity) throws Exception {
        // 수정 후처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 (dto level)
     *
     * @param modifyDto 수정할 DTO 객체
     * @return Dto - 수정된 결과를 DTO로 변환한 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    default Dto modify(final Dto modifyDto) throws Exception {
        // 수정 전처리
        this.preModify(modifyDto);

        // Entity 레벨 조회
        final Mapstruct mapstruct = this.getMapstruct();
        final Entity modifyEntity = this.getDtlEntity(modifyDto);
        mapstruct.updateFromDto(modifyDto, modifyEntity);

        // 수정 중간처리
        this.midModify(modifyEntity);

        // update
        final Repository repository = this.getRepository();
        final Entity updatedEntity = repository.saveAndFlush(modifyEntity);

        // 수정 후처리
        this.postModify(updatedEntity);

        return mapstruct.toDto(updatedEntity);
    }

    /**
     * default: 게시물 수정 (entity level)
     *
     * @param modifyEntity - 수정할 엔티티 객체
     * @return updatedEntity - 저장 및 새로고침된 엔티티 객체
     */
    @Transactional
    default Entity updt(final Entity modifyEntity) {
        final Repository repository = this.getRepository();
        final Entity updatedEntity = repository.saveAndFlush(modifyEntity);
        try {
            repository.refresh(updatedEntity);
        } catch (EntityNotFoundException ex) {
            ex.getStackTrace();
        }

        return updatedEntity;
    }

    /**
     * default: 게시물 삭제 전처리
     *
     * @param deleteEntity - 삭제할 엔티티 객체
     * @throws Exception 삭제 전처리 중 발생할 수 있는 예외
     */
    default void preDelete(final Entity deleteEntity) throws Exception {
        // 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 삭제 (Dto 사용)
     *
     * @param deleteDto 삭제할 DTO 객체
     * @return Boolean - 삭제 성공시 true, 실패 시 false
     * @throws Exception 삭제 중 발생할 수 있는 예외
     */
    @Transactional
    default Boolean delete(final Dto deleteDto) throws Exception {
        return this.delete(deleteDto.getKey());
    }

    /**
     * default: 게시물 삭제 (key 사용)
     *
     * @param key 삭제할 엔티티의 키
     * @return Boolean - 삭제 성공시 true, 실패 시 false
     * @throws Exception 삭제 중 발생할 수 있는 예외
     */
    @Transactional
    default Boolean delete(final Key key) throws Exception {
        final Repository repository = this.getRepository();
        final Entity deleteEntity = this.getDtlEntity(key);
        if (deleteEntity == null) return false;

        this.preDelete(deleteEntity);

        repository.delete(deleteEntity);

        this.postDelete(deleteEntity);

        return true;
    }

    /**
     * default: 게시물 삭제 후처리
     *
     * @param deletedEntity - 삭제된 엔티티 객체
     * @throws Exception 후처리 중 발생할 수 있는 예외
     */
    default void postDelete(final Entity deletedEntity) throws Exception {
        // 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 bulk-delete (entity level)
     *
     * @param deleteEntityList - 삭제할 엔티티 리스트
     * @return Boolean - 삭제 성공시 true
     */
    @Transactional
    default boolean deleteAll(final List<Entity> deleteEntityList) {
        final Repository repository = this.getRepository();
        repository.deleteAll(deleteEntityList);

        return true;
    }

    /**
     * default: 게시물 bulk-delete (entity level)
     *
     * @param searchParamMap 엔티티 리스트를 조회할 검색 파라미터 맵
     * @return Boolean - 삭제 성공시 true
     * @throws Exception 삭제 중 발생할 수 있는 예외
     */
    @Transactional
    default boolean deleteAll(final Map<String, Object> searchParamMap) throws Exception {
        final List<Entity> deleteEntityList = this.getListEntity(searchParamMap);
        final Repository repository = this.getRepository();
        repository.deleteAll(deleteEntityList);

        // 벌크 삭제 후처리
        this.postDeleteAll(deleteEntityList);

        return true;
    }

    /**
     * default: 게시물 bulk 삭제 후처리
     *
     * @param deletedEntityList - 삭제된 엔티티 리스트
     */
    default void postDeleteAll(final List<Entity> deletedEntityList) {
        // 기본 공백, 필요시 각 함수에서 Override
    }
}
