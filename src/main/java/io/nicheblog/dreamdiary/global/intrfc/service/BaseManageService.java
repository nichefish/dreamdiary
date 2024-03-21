package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseManageEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
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
public interface BaseManageService<Dto extends BaseAuditDto, ListDto extends BaseAuditDto, Key extends Serializable, Entity extends BaseManageEntity, Repository extends BaseRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseListMapstruct<Dto, ListDto, Entity>>
        extends BaseCrudService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    /**
     * default: 사용 상태로 변경
     */
    default Boolean setInUse(final Key key) throws Exception {
        Entity e = this.getDtlEntity(key);
        e.setUseYn("Y");
        this.updt(e);
        return true;
    }

    /**
     * default: 미사용 상태로 변경
     */
    default Boolean setInUnuse(final Key key) throws Exception {
        Entity e = this.getDtlEntity(key);
        e.setUseYn("N");
        this.updt(e);
        return true;
    }
}
