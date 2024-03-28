package io.nicheblog.dreamdiary.web.model.flsys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * FlsysCmmDto
 * <pre>
 *  파일시스템 공통요소 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FlsysCmmDto
        implements Comparable<FlsysCmmDto>, Serializable {

    private File file;
    private FlsysMetaDto meta;

    /**
     * 파일명
     */
    private String fileNm;
    /**
     * 절대경로
     */
    private String filePath;
    /**
     * 상위절대경로
     */
    private String upperPath;

    private List<FlsysDirDto> dirList;
    private List<FlsysFileDto> fileList;

    /* ----- */

    /**
     * 생성자
     */
    public FlsysCmmDto(final File file) throws IOException {
        this.file = file;
        this.upperPath = (file.getParent() != null ? file.getParent() : "/").replace("\\", "/");
        this.fileNm = file.getName()
                          .replace("\\", "/");
        this.filePath = file.getPath()
                            .replace("\\", "/");
    }

    public Boolean getHasMeta() {
        return this.meta != null;
    }

    @Override
    public int compareTo(final @NotNull FlsysCmmDto compare) {
        return this.fileNm.compareTo(compare.getFileNm());
    }
}
