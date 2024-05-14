package io.nicheblog.dreamdiary.web.model.cmm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BaseTagifyDto
 * <pre>
 *  for tagify jsonstring parse.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseTagifyDto {

    /** value */
    private String value;

    /** data */
    private BaseTagifyDataDto data;
}
