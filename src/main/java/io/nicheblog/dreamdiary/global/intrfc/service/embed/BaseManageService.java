package io.nicheblog.dreamdiary.global.intrfc.service.embed;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.StateEmbedModule;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;

import java.io.Serializable;

/**
 * BaseManageService
 * <pre>
 *  (공통/상속) 관리요소 CRUD 공통 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
public interface BaseManageService<Dto extends BaseAuditDto & StateCmpstnModule, ListDto extends BaseAuditDto & StateCmpstnModule, Key extends Serializable, Entity extends BaseCrudEntity & StateEmbedModule, Repository extends BaseRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseListMapstruct<Dto, ListDto, Entity>>
        extends BaseCrudService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    /**
     * default: 사용 상태로 변경
     */
    default Boolean setStateUse(final Key key) throws Exception {
        Entity e = this.getDtlEntity(key);
        e.getState().setUseYn("Y");
        this.updt(e);
        // 변경 후처리
        this.postSetState();

        return true;
    }

    /**
     * default: 미사용 상태로 변경
     */
    default Boolean setStateUnuse(final Key key) throws Exception {
        Entity e = this.getDtlEntity(key);
        e.getState().setUseYn("N");
        this.updt(e);
        // 변경 후처리
        this.postSetState();

        return true;
    }

    /**
     * default: 사용/미사용 상태로 변경 후처리
     */
    default void postSetState() throws Exception {
        // 변경 후처리:: 기본 공백, 필요시 각 함수에서 Override
    }
}
