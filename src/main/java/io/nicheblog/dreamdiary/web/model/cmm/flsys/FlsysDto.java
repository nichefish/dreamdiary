package io.nicheblog.dreamdiary.web.model.cmm.flsys;

import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
public class FlsysDto
        implements Comparable<FlsysDto>, Serializable {

    private File file;
    private FlsysMetaDto meta;

    /** 파일명 */
    private String fileNm;
    /** 절대경로 */
    private String filePath;
    /** 상위절대경로 */
    private String upperPath;

    private List<String> filePathStrList;

    /** 하위 폴더 목록 */
    private List<FlsysDirDto> dirList;
    /** 하위 파일 목록 */
    private List<FlsysFileDto> fileList;

    /** lastModified */
    private String lastModified;

    /* ----- */

    /**
     * 생성자
     */
    public FlsysDto(final File file) throws Exception {
        this.file = file;
        this.upperPath = (file.getParent() != null ? file.getParent() : "/").replace("\\", "/");
        this.fileNm = file.getName()
                          .replace("\\", "/");
        this.filePath = file.getPath()
                            .replace("\\", "/");

        this.filePathStrList = Arrays.stream(this.filePath.split("/"))
                .collect(Collectors.toList());
        Timestamp timestamp = new Timestamp(file.lastModified());
        this.lastModified = DateUtils.asStr(new Date(timestamp.getTime()), DatePtn.DATETIME);
    }

    public Boolean getHasMeta() {
        return this.meta != null;
    }

    @Override
    public int compareTo(final @NotNull FlsysDto compare) {
        return this.fileNm.compareTo(compare.getFileNm());
    }
}
