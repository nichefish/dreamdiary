package io.nicheblog.dreamdiary.domain._core.cd.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseParam;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * DtlCdSearchParam
 * <pre>
 *  상세 코드 목록 검색 파라미터.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
public class DtlCdSearchParam
        extends BaseSearchParam {

    /** 분류 코드 */
    private String clCd;
}
