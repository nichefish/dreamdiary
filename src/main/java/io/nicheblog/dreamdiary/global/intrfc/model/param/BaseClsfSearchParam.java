package io.nicheblog.dreamdiary.global.intrfc.model.param;

import lombok.*;

/**
 * BaseClsfSearchParam
 * <pre>
 *  (공통/상속) 게시물 목록 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseSearchParam
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class BaseClsfSearchParam
        extends BaseSearchParam {

    /** 컨텐츠 타입 */
    private String contentType;

    // TODO: 태그 등 모듈 관련 검색 요소
}
