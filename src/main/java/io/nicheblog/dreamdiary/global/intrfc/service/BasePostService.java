package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BasePostService
 * <pre>
 *  (공통/상속) 일반게시물 CRUD 공통 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
public interface BasePostService<Dto extends BasePostDto & Identifiable<Key>, ListDto extends BasePostDto, Key extends Serializable, Entity extends BasePostEntity, Repository extends BaseStreamRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseClsfMapstruct<Dto, ListDto, Entity>>
        extends BaseClsfService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    /**
     * default: 상단 고정 항목 목록 조회
     * TODO: 조회조건 좀 더 세분화?
     */
    default List<ListDto> getFxdList() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("fxdYn", "Y");
        }};
        return this.getListDto(searchParamMap);
    }

    /**
     * default: 항목 조회수 증가
     */
    default void hitCntUp(final Key key) throws Exception {
        Entity e = this.getDtlEntity(key);
        String lgnUserId = AuthUtils.getLgnUserId();
        if (StringUtils.isEmpty(lgnUserId)) return;
        if (lgnUserId.equals(e.getRegstrId())) return;      // 본인이 쓴 글은 조회수 증가하지 않음
        Integer currentHitCnt = e.getHitCnt();
        e.setHitCnt(currentHitCnt + 1);
        this.updt(e);
    }
}
