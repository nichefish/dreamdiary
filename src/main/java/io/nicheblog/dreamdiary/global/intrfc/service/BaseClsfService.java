package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbedModule;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.ManagtCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;

import java.io.Serializable;

/**
 * BaseClsfService
 * <pre>
 *  (공통/상속) 일반게시물 CRUD 공통 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
public interface BaseClsfService<Dto extends BaseClsfDto, ListDto extends BaseClsfDto, Key extends Serializable, Entity extends BaseClsfEntity, Repository extends BaseRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseClsfMapstruct<Dto, ListDto, Entity>>
        extends BaseMultiCrudService<Dto, ListDto, Key, Entity, Repository, Spec, Mapstruct> {

    //
    @Override
    /**
     * default: 게시물 등록 (dto level)
     */
    default Dto regist(final Dto dto) throws Exception {
        // 등록 전처리
        this.preRegist(dto);

        // Dto -> Entity
        Mapstruct mapstruct = this.getMapstruct();
        Entity entity = mapstruct.toEntity(dto);

        // managt 처리
        if (dto instanceof ManagtCmpstnModule && entity instanceof ManagtEmbedModule) {
            ((ManagtEmbedModule) entity).setManagt(new ManagtEmbed(true));
        }

        // insert
        Entity rslt = this.updt(entity);

        // 등록 후처리
        this.postRegist(rslt);

        return mapstruct.toDto(rslt);
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

        // managt 처리
        if (dto instanceof ManagtCmpstnModule && entity instanceof ManagtEmbedModule) {
            // (수정시) 조치일자 변경하지 않음 처리
            boolean isManagtDtNull = ((ManagtEmbedModule) entity).getManagt() == null || ((ManagtEmbedModule) entity).getManagt().getManagtDt() == null;
            boolean updtManagtDt = isManagtDtNull || "Y".equals(dto.getManagtDtUpdtYn());
            ((ManagtEmbedModule) entity).setManagt(new ManagtEmbed(updtManagtDt));
        }

        // update
        Repository repository = this.getRepository();
        Entity rslt = repository.saveAndFlush(entity);

        // 수정 후처리
        this.postModify(rslt);

        return mapstruct.toDto(rslt);
    }
}
