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
public interface BaseAuditListMapstruct<Dto extends BaseAuditDto, ListDto extends BaseAuditDto, Entity extends BaseAuditEntity>
        extends BaseListMapstruct<Dto, ListDto, Entity> {

    @AfterMapping
    default void mapListAuditFields(final Entity entity, final @MappingTarget ListDto dto) throws Exception {
        // 공통 필드 매핑 로직
        dto.setRegstrNm(entity.getRegstrInfo() != null ? entity.getRegstrInfo().getNickNm() : null);
        dto.setMdfusrNm(entity.getMdfusrInfo() != null ? entity.getMdfusrInfo().getNickNm() : null);
        dto.setRegDt(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME));
        dto.setMdfDt(DateUtils.asStr(entity.getMdfDt(), DateUtils.PTN_DATETIME));
    }
}
