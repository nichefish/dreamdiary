package io.nicheblog.dreamdiary.global.intrfc.model.param;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * BaseClsfSearchParam
 * <pre>
 *  (공통/상속) 게시물 목록 검색 파라미터.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BaseClsfSearchParam
        extends BaseSearchParam {

    /** 컨텐츠 타입 */
    private String contentType;

    /** 태그 검색 */
    private List<Integer> tags;
}
