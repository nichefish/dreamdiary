package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.model.DtlCdDto;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseParam;
import lombok.*;

import java.util.List;

/**
 * DtlCdParam
 * <pre>
 *  상세코드 파라미터 Dto
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
public class DtlCdParam
        extends BaseParam {

    /** 정렬순서 배열 */
    List<DtlCdDto> sortOrdr;
}
