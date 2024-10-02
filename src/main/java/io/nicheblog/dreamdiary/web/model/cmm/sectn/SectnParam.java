package io.nicheblog.dreamdiary.web.model.cmm.sectn;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * SectnParam
 * <pre>
 *  단락 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostSearchParam
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SectnParam
        extends BaseParam {

    /** 정렬순서 배열 */
    private List<SectnDto> sortOrdr;
}
