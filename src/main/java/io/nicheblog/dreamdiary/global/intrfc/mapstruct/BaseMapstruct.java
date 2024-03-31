package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditRegDto;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * BaseMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseMapstruct<Dto, Entity> {

    /**
     * Entity -> Dto
     */
    Dto toDto(final Entity e) throws Exception;

    /**
     * Dto -> Entity
     */
    Entity toEntity(final Dto d) throws Exception;

    /**
     * update Entity from Dto
     * (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final Dto d,
            final @MappingTarget Entity e
    ) throws Exception;

    /**
     * Audit 날짜 매핑
     */
    @AfterMapping
    default void mapAuditFields(final Entity entity, final @MappingTarget Dto dto) throws Exception {
        // 공통 필드 매핑 로직
        if (entity instanceof BaseAuditRegEntity && dto instanceof BaseAuditRegDto) {
            ((BaseAuditRegDto) dto).setRegDt(DateUtils.asStr(((BaseAuditRegEntity) entity).getRegDt(), DateUtils.PTN_DATETIME));
        }
        // 공통 필드 매핑 로직
        if (entity instanceof BaseAuditEntity && dto instanceof BaseAuditDto) {
            ((BaseAuditDto) dto).setMdfDt(DateUtils.asStr(((BaseAuditEntity) entity).getMdfDt(), DateUtils.PTN_DATETIME));
        }
    }
}
