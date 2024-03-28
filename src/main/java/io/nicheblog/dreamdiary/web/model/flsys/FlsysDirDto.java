package io.nicheblog.dreamdiary.web.model.flsys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * FlsysDirDto
 * <pre>
 *  파일시스템 디렉토리 요소 Dto
 * </pre>
 *
 * @author nichefish
 * @extends FlsysCmmDto
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FlsysDirDto
        extends FlsysCmmDto
        implements Serializable {
    //

    /* ----- */

    /**
     * 생성자
     */
    public FlsysDirDto(final File file) throws IOException {
        super(file);
    }
}
