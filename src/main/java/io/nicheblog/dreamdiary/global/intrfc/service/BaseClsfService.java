package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.file.service.CmmFileService;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfListDto;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * BaseClsfService
 * <pre>
 *  (공통/상속) 일반게시물 CRUD 공통 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
public interface BaseClsfService<Dto extends BaseClsfDto, ListDto extends BaseClsfListDto, Key extends Serializable, Entity extends BaseClsfEntity, Repository extends BaseRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseListMapstruct<Dto, ListDto, Entity>, FileService extends CmmFileService>
        extends BaseMultiCrudService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct, FileService> {

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
        // TODO: comment and tag
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
        // TODO: comment and tag
        // update
        Repository repository = this.getRepository();
        Entity rsltEntity = repository.save(entity);
        return mapstruct.toDto(rsltEntity);
    }

}
