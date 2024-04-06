package io.nicheblog.dreamdiary.web.mapstruct.cmm.managt;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.MapstructHelper;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.managt.ManagtrEntity;
import io.nicheblog.dreamdiary.web.model.cmm.managtr.ManagtrDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ManagtrMapstruct
 * <pre>
 *  일반게시판 게시물 작업자 MapStruct 기반 Mapper 인터페이스
 *  게시물 게시물 작업자(managtr) = 일반게시판 수정이력자. 일반게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class})
public interface ManagtrMapstruct
        extends BaseMapstruct<ManagtrDto, ManagtrEntity> {

    ManagtrMapstruct INSTANCE = Mappers.getMapper(ManagtrMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DatePtn.DATETIME))")
    @Mapping(target = "regstrNm", expression = "java(entity.getRegstrInfo() != null ? entity.getRegstrInfo().getNickNm() : null)")
    ManagtrDto toDto(final ManagtrEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    ManagtrEntity toEntity(final ManagtrDto dto) throws Exception;

    /**
     * default : BaseEntity 기본 요소들 매핑
     */
    @AfterMapping
    default void mapBaseFields(final ManagtrEntity entity, final @MappingTarget ManagtrDto dto) throws Exception {
        MapstructHelper.mapBaseFields(entity, dto);
    }
}
