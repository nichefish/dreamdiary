package io.nicheblog.dreamdiary.global.intrfc.model.tagify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BaseTagifyDataDto
 * <pre>
 *  Tagify JsonString - { data } 파싱 Dto.
 *  Dto for tagify jsonstring { data } parse.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseTagifyDataDto {

    /** ctgr */
    private String ctgr;
}
