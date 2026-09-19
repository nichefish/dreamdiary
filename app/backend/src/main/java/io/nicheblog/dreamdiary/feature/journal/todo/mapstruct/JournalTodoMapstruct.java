package io.nicheblog.dreamdiary.feature.journal.todo.mapstruct;

import io.nicheblog.dreamdiary.feature.attachable._shared.mapstruct.BaseAttachableMapstruct;
import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.PrefixEntity;
import io.nicheblog.dreamdiary.feature.attachable.prefix.model.PrefixDto;
import io.nicheblog.dreamdiary.feature.journal.todo.entity.JournalTodoEntity;
import io.nicheblog.dreamdiary.feature.journal.todo.model.JournalTodoDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseWriteMapstruct;
import io.nicheblog.dreamdiary.global.util.MarkdownUtils;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JournalTodoMapstruct
 * <pre>
 *  저널 꿈 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class, MarkdownUtils.class}, builder = @Builder(disableBuilder = true))
public interface JournalTodoMapstruct
        extends BaseWriteMapstruct<JournalTodoDto, JournalTodoEntity>, BaseAttachableMapstruct<JournalTodoDto, JournalTodoEntity> {

    JournalTodoMapstruct INSTANCE = Mappers.getMapper(JournalTodoMapstruct.class);

    /**
     * Entity -> Dto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return Dto -- 변환된 Dto 객체
     */
    @Override
    @Named("toDto")
    @Mapping(target = "markdownContent", expression = "java(StringUtils.isEmpty(entity.getContent()) ? \"-\" : MarkdownUtils.markdown(entity.getContent()))")
    @Mapping(target = "prefix", expression = "java(selectedPrefixToDto(entity.getSelectedPrefix()))")
    @Mapping(target = "prefixId", expression = "java(entity.getSelectedPrefixId())")
    @Mapping(target = "expireDt", expression = "java(DateUtils.asStr(entity.getExpireDt(), DatePtn.DATE))")
    JournalTodoDto toDto(final JournalTodoEntity entity) throws Exception;

    /**
     * prefix_content 연결에서 조립된 선택 말머리를 표시용 DTO로 변환한다.
     *
     * @param prefix 선택된 말머리 엔티티
     * @return 표시용 말머리 DTO. 선택이 없으면 null.
     */
    default PrefixDto selectedPrefixToDto(final PrefixEntity prefix) {
        if (prefix == null) return null;
        return PrefixDto.displayOf(
                    prefix.getId(),
                    prefix.getName(),
                    prefix.getTextClass(),
                    prefix.getSortOrder(),
                    prefix.getActiveYn()
                );
    }

    /**
     * Dto -> Entity 변환
     *
     * @param dto 변환할 Dto 객체
     * @return Entity -- 변환된 Entity 객체
     */
    @Override
    @Mapping(target = "content", expression = "java(MarkdownUtils.normalize(dto.getContent()))")
    @Mapping(target = "prefix", ignore = true)
    @Mapping(target = "expireDt", expression = "java(DateUtils.asLocalDate(dto.getExpireDt()))")
    JournalTodoEntity toEntity(final JournalTodoDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     * <p>
     * {@code expireDt} 는 expression 매핑이라 이 null 무시 규칙에서 벗어나 항상 반영된다.
     * 빈 문자열이 null 로 변환되어 그대로 기록되므로 기한 해제가 성립한다.
     * </p>
     *
     * @param dto 업데이트할 Dto 객체
     * @param entity 업데이트할 대상 Entity 객체
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "content", expression = "java(MarkdownUtils.normalize(dto.getContent()))")
    @Mapping(target = "prefix", ignore = true)
    @Mapping(target = "expireDt", expression = "java(DateUtils.asLocalDate(dto.getExpireDt()))")
    void updateFromDto(final JournalTodoDto dto, final @MappingTarget JournalTodoEntity entity) throws Exception;
}
