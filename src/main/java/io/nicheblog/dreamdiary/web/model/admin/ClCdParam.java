package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.model.ClCdDto;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ClCdParam
        extends BaseParam {

    /** 정렬순서 배열 */
    List<ClCdDto> sortOrdr;
}
