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
     * @param dto 등록할 DTO 객체
     * @throws Exception 전처리 중 발생할 수 있는 예외
     */
    default void preRegist(final Dto dto) throws Exception {
        // 등록 전처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 등록 후처리 (entity level, entity 변환 후 처리)
     *
     * @param rslt - 등록된 엔티티 객체
     * @throws Exception 후처리 중 발생할 수 있는 예외
     */
    default void postRegist(final Entity rslt) throws Exception {
        // 등록 후처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 등록 (dto level)
     *
     * @param dto 등록할 DTO 객체
     * @return {@link Dto} -- 등록 결과를 DTO로 변환한 객체
     * @throws Exception 등록 중 발생할 수 있는 예외
     */
    @Transactional
    default Dto regist(final Dto dto) throws Exception {
        // 등록 전처리
        this.preRegist(dto);

        // Dto -> Entity 변환
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
     *
     * @param entityList - 등록할 엔티티 리스트
     * @return {@link Boolean} -- 등록 성공시 true
     */
    @Transactional
    default boolean registAll(final List<Entity> entityList) {
        Repository repository = this.getRepository();
        List<Entity> rsEntityList = repository.saveAllAndFlush(entityList);

        // 벌크 등록 후처리
        this.postRegistAll(rsEntityList);
        
        return true;
    }

    /**
     * default: 게시물 bulk 등록 후처리
     * @param entityList - 등록된 엔티티 리스트
     */
    default void postRegistAll(final List<Entity> entityList) {
        // 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 전처리 (dto level)
     * @param dto 수정할 DTO 객체
     * @throws Exception 수정 전처리 중 발생할 수 있는 예외
     */
    default void preModify(final Dto dto) throws Exception {
        // 수정 전처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 중간처리 (entity level, entity 변환 후 처리)
     * @param entity 수정 중간처리를 할 엔티티 객체
     */
    default void midModify(final Entity entity) {
        // 수정 중간처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 후처리 (entity level, entity 변환 후 처리)
     * @param rslt - 수정된 엔티티 객체
     * @throws Exception 후처리 중 발생할 수 있는 예외
     */
    default void postModify(final Entity rslt) throws Exception {
        // 수정 후처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 수정 (dto level)
     * @param dto 수정할 DTO 객체
     * @return Dto - 수정된 결과를 DTO로 변환한 객체
     * @throws Exception 수정 중 발생할 수 있는 예외
     */
    @Transactional
    default Dto modify(final Dto dto) throws Exception {
        // 수정 전처리
        this.preModify(dto);

        // Entity 레벨 조회
        Mapstruct mapstruct = this.getMapstruct();
        Entity entity = this.getDtlEntity(dto);
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
     * @param e - 수정할 엔티티 객체
     * @return Entity - 저장 및 새로고침된 엔티티 객체
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
     * @param e - 삭제할 엔티티 객체
     * @throws Exception 삭제 전처리 중 발생할 수 있는 예외
     */
    default void preDelete(final Entity e) throws Exception {
        // 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 삭제 (Dto 사용)
     * @param dto 삭제할 DTO 객체
     * @return Boolean - 삭제 성공시 true, 실패 시 false
     * @throws Exception 삭제 중 발생할 수 있는 예외
     */
    default Boolean delete(final Dto dto) throws Exception {
        return this.delete(dto.getKey());
    }

    /**
     * default: 게시물 삭제 (key 사용)
     * @param key 삭제할 엔티티의 키
     * @return Boolean - 삭제 성공시 true, 실패 시 false
     * @throws Exception 삭제 중 발생할 수 있는 예외
     */
    default Boolean delete(final Key key) throws Exception {
        Repository repository = this.getRepository();
        Entity e = this.getDtlEntity(key);
        if (e == null) return false;

        this.preDelete(e);

        repository.delete(e);

        this.postDelete(e);

        return true;
    }

    /**
     * default: 게시물 삭제 후처리
     * @param e - 삭제된 엔티티 객체
     * @throws Exception 후처리 중 발생할 수 있는 예외
     */
    default void postDelete(final Entity e) throws Exception {
        // 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * default: 게시물 bulk-delete (entity level)
     * @param entityList - 삭제할 엔티티 리스트
     * @return Boolean - 삭제 성공시 true
     */
    default boolean deleteAll(final List<Entity> entityList) {
        Repository repository = this.getRepository();
        repository.deleteAll(entityList);
        return true;
    }

    /**
     * default: 게시물 bulk-delete (entity level)
     * @param searchParamMap 엔티티 리스트를 조회할 검색 파라미터 맵
     * @return Boolean - 삭제 성공시 true
     * @throws Exception 삭제 중 발생할 수 있는 예외
     */
    default boolean deleteAll(final Map<String, Object> searchParamMap) throws Exception {
        List<Entity> entityList = this.getListEntity(searchParamMap);
        Repository repository = this.getRepository();
        repository.deleteAll(entityList);

        // 벌크 삭제 후처리
        this.postDeleteAll(entityList);
        return true;
    }

    /**
     * default: 게시물 bulk 삭제 후처리
     * @param entityList - 삭제된 엔티티 리스트
     */
    default void postDeleteAll(final List<Entity> entityList) {
        // 기본 공백, 필요시 각 함수에서 Override
    }
}
