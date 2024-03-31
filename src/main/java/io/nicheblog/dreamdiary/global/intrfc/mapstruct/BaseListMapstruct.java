package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditRegDto;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

/**
 * BaseListMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 *  (entity -> listDto 추가)
 * <pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
public interface BaseListMapstruct<Dto, ListDto, Entity>
        extends BaseMapstruct<Dto, Entity> {

    /**
     * Entity -> ListDto
     */
    ListDto toListDto(final Entity e) throws Exception;

    /**
     * Audit 날짜 매핑
     */
    @AfterMapping
    default void mapAuditListFields(final Entity entity, final @MappingTarget ListDto dto) throws Exception {
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
