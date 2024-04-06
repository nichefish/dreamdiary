package io.nicheblog.dreamdiary.web.model.cmm.flsys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
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
        extends FlsysDto
        implements Serializable {

    /** lastModified */
    private Long totalSpace;
    /** lastModified */
    private Long usableSpace;
    /** lastModified */
    private Long freeSpace;

    /* ----- */

    /**
     * 생성자
     */
    public FlsysDirDto(final File file) throws Exception {
        super(file);
        this.totalSpace = file.getTotalSpace();
        this.usableSpace = file.getUsableSpace();
        this.freeSpace = file.getFreeSpace();
    }
}
