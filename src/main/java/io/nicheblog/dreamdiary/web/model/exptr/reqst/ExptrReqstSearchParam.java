package io.nicheblog.dreamdiary.web.model.exptr.reqst;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;

/**
 * ExptrReqstSearchParam
 * <pre>
 *  경비 관리 > 물품구매/경조사비 신청 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostSearchParam
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ExptrReqstSearchParam
        extends BasePostSearchParam {

    /** 컨텐츠 타입 */
    private String contentType = ContentType.EXPTR_REQST.key;
}
