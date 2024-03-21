package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

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
}
