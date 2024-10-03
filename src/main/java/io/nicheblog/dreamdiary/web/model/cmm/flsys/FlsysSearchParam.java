package io.nicheblog.dreamdiary.web.model.cmm.flsys;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * FlsysSearchParam
 * <pre>
 *  파일시스템 목록 검색 파라미터 Dto
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
public class FlsysSearchParam
        extends BasePostSearchParam {

    /** 참조 글 번호 */
    @Positive
    private Integer refPostNo;

    /** 참조 컨텐츠 타입 */
    @Size(max = 50)
    private String refContentType;
}
