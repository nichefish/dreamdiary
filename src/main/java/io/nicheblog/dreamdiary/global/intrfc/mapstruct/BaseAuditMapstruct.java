package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

/**
 * BaseMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseAuditMapstruct<Dto extends BaseAuditDto, Entity extends BaseAuditEntity>
        extends BaseMapstruct<Dto, Entity> {

    @AfterMapping
    default void mapAuditFields(final Entity entity, final @MappingTarget Dto dto) throws Exception {
        // 공통 필드 매핑 로직
        dto.setRegDt(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME));
        dto.setMdfDt(DateUtils.asStr(entity.getMdfDt(), DateUtils.PTN_DATETIME));
    }
}
