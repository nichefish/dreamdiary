package io.nicheblog.dreamdiary.domain.admin.menu.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * MenuParam
 * <pre>
 *  메뉴 검색 파라미터 Dto.
 * </pre>
 *
 * @author nichefish
 * @extends BaseParam
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuParam
        extends BaseParam {

    /** 정렬 순서 */
    List<MenuDto> sortOrdr;
}
