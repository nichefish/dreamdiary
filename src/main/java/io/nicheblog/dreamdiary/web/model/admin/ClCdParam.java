package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCd;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseParam;
import lombok.*;

import java.util.List;

/**
 * ClCdParam
 * <pre>
 *  분류코드 파라미터 Dto
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
public class ClCdParam
        extends BaseParam {

    /** 정렬순서 배열 */
    List<ClCd> sortOrdr;
}
