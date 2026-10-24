package io.nicheblog.dreamdiary.feature.prospective.keep.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * ProspectiveKeepSearchParam
 * <pre>
 *  Prospective Keep 목록 검색 파라미터.
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
public class ProspectiveKeepSearchParam
        extends BaseSearchParam {

    /** 컨텐츠 타입 */
    private String contentType;

    /** 태그 ID */
    private Integer tagId;

    /** 특정 Project의 Keep만 조회한다. */
    private Integer projectId;

    /** true이면 Project 미지정 Keep만 조회한다. */
    private Boolean projectUnassigned;
}
