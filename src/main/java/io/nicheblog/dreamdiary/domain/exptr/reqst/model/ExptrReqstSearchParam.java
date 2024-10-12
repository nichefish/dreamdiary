package io.nicheblog.dreamdiary.domain.exptr.reqst.model;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * ExptrReqstSearchParam
 * <pre>
 *  경비 관리 > 물품구매/경조사비 신청 검색 파라미터.
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
public class ExptrReqstSearchParam
        extends BasePostSearchParam {

    /** 컨텐츠 타입 */
    private String contentType = ContentType.EXPTR_REQST.key;
}
