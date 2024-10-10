package io.nicheblog.dreamdiary.domain.vcatn.papr.model;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;

/**
 * VcatnPaprSearchParam
 * <pre>
 *  휴가계획서 목록 검색 파라미터.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
public class VcatnPaprSearchParam
        extends BasePostSearchParam {

    /** 컨텐츠 타입 */
    private String contentType = ContentType.VCATN_PAPR.key;
}
