package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.file.service.AtchFileService;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
public interface BasePostService<Dto extends BasePostDto, ListDto extends BasePostListDto, Key extends Serializable, Entity extends BasePostEntity, Repository extends BaseRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseListMapstruct<Dto, ListDto, Entity>, FileService extends AtchFileService>
        extends BaseClsfService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct, AtchFileService> {

    /**
     * default: 상단 고정 항목 목록 조회
     * TODO: 조회조건 좀 더 세분화?
     */
    default List<ListDto> getFxdList() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("fxdYn", "Y");
        }};
        Page<ListDto> dtoPage = this.getListDto(searchParamMap, Pageable.unpaged());
        return dtoPage.getContent();
    }

    /**
     * default: 항목 등록 (Multipart)
     * managt(최종수정자) 정보 포함하도록 재정의
     */
    @Override
    default Dto regist(final Dto dto) throws Exception {
        this.preRegist(dto);

        Mapstruct mapstruct = this.getMapstruct();
        // Dto -> Entity
        Entity entity = mapstruct.toEntity(dto);
        // managt
        // entity.setManagtrId(AuthUtils.getLgnUserId());
        // entity.setManagtDt(DateUtils.getCurrDate());
        // insert
        Entity rslt = this.updt(entity);
        return mapstruct.toDto(rslt);
    }

    /**
     * default: 항목 수정 (Multipart)
     * managt(최종수정자) 정보 포함하도록 재정의
     */
    @Override
    default Dto modify(
            final Dto dto,
            final Key key
    ) throws Exception {
        this.preModify(dto);

        Mapstruct mapstruct = this.getMapstruct();
        // Entity 레벨 조회
        Entity entity = this.getDtlEntity(key);
        mapstruct.updateFromDto(dto, entity);
        // managt
        // if ("Y".equals(dto.getManagtDtUpdtYn())) {
        //     entity.setManagtrId(AuthUtils.getLgnUserId());
        //     entity.setManagtDt(DateUtils.getCurrDate());
        // }
        // update
        Repository repository = this.getRepository();
        Entity rsltEntity = repository.save(entity);
        return mapstruct.toDto(rsltEntity);
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
