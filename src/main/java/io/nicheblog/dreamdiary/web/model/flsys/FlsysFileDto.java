package io.nicheblog.dreamdiary.web.model.flsys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * FlsysFileDto
 * <pre>
 *  파일시스템 파일 요소 Dto
 * </pre>
 *
 * @author nichefish
 * @extends FlsysCmmDto
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FlsysFileDto
        extends FlsysCmmDto
        implements Serializable {
    //
    /**
     * 실행가능여부
     */
    private Boolean isExcecutable;
    /**
     * VOD 여부
     */
    private Boolean isVod;

    /* ----- */

    /**
     * 생성자
     */
    public FlsysFileDto(final File file) throws IOException {
        super(file);
        this.isExcecutable = file.canExecute();
    }
}
