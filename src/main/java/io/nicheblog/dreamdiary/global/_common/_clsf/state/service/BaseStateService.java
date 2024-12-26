package io.nicheblog.dreamdiary.global._common._clsf.state.service;

import io.nicheblog.dreamdiary.global._common._clsf.state.entity.embed.StateEmbedModule;
import io.nicheblog.dreamdiary.global._common._clsf.state.model.cmpstn.StateCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

/**
 * BaseStateService
 * <pre>
 *  (공통/상속) 관리요소 CRUD 공통 서비스 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
public interface BaseStateService<Dto extends BaseAuditDto & StateCmpstnModule & Identifiable<Key>, ListDto extends BaseAuditDto & StateCmpstnModule, Key extends Serializable, Entity extends BaseCrudEntity & StateEmbedModule, Repository extends BaseStreamRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseCrudMapstruct<Dto, ListDto, Entity>>
        extends BaseCrudService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    /**
     * default: 상태를 '사용'으로 변경한다.
     * 
     * @param key 상태를 변경할 엔티티의 키
     * @return Boolean - 상태 변경 성공시 true
     * @throws Exception 상태 변경 중 발생할 수 있는 예외
     */
    @Transactional
    default Boolean setStateUse(final Key key) throws Exception {
        Entity e = this.getDtlEntity(key);
        e.getState().setUseYn("Y");
        this.updt(e);
        // 변경 후처리
        this.postSetState();

        return true;
    }

    /**
     * default: 상태를 '미사용'으로 변경한다.
     * 
     * @param key 상태를 변경할 엔티티의 키
     * @return Boolean - 상태 변경 성공시 true
     * @throws Exception 상태 변경 중 발생할 수 있는 예외
     */
    @Transactional
    default Boolean setStateUnuse(final Key key) throws Exception {
        Entity e = this.getDtlEntity(key);
        e.getState().setUseYn("N");
        this.updt(e);
        // 변경 후처리
        this.postSetState();

        return true;
    }

    /**
     * default: 상태 변경 후 해당 로직을 수행한다.
     *
     * @throws Exception 후처리 중 발생할 수 있는 예외
     */
    default void postSetState() throws Exception {
        // 변경 후처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * 정렬 순서 업데이트.
     *
     * @param sortOrdr 키 + 정렬 순서로 이루어진 목록
     * @return {@link Boolean} -- 성공시 true 반환
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    default Boolean sortOrdr(final List<Dto> sortOrdr) throws Exception {
        if (CollectionUtils.isEmpty(sortOrdr)) return true;
        sortOrdr.forEach(dto -> {
            try {
                final Entity e = this.getDtlEntity(dto.getKey());
                e.getState().setSortOrdr(dto.getState().getSortOrdr());
                this.updt(e);
            } catch (Exception ex) {
                ex.getStackTrace();
                // 로그 기록, 예외 처리 등
                throw new RuntimeException(ex);
            }
        });
        return true;
    }
}