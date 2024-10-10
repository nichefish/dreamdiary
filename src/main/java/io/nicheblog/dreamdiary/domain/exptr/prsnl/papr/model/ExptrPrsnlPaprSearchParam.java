package io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;

/**
 * ExptrPrsnlPaprSearchParam
 * <pre>
 *  경비 관리 > 경비지출서 > 경비지출서 목록 검색 파라미터.
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
public class ExptrPrsnlPaprSearchParam
        extends BasePostSearchParam {

    /** 컨텐츠 타입 */
    private String contentType = ContentType.EXPTR_PRSNL_PAPR.key;
}
