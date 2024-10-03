package io.nicheblog.dreamdiary.global.intrfc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.Positive;
import java.util.List;

/**
 * BaseAtchDto
 * <pre>
 *  (공통/상속) 첨부파일 소유 Dto (첨부파일 정보 추가)
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Log4j2
public class BaseAtchDto
        extends BaseAuditDto {

    /** 첨부파일 번호 */
    @Positive
    protected Integer atchFileNo;

    /** 첨부파일 정보 (1:N) */
    protected AtchFileDto atchFileInfo;

    /** 첨부파일 .zip 다운로드 URL */
    protected String atchFileZipUrl;
    /** 첨부파일 .zip 파일명 */

    @JsonIgnore
    protected String atchFileZipNm;

    /** 첨부파일 존재 여부 */
    protected Boolean hasAtchFile;

    /* ---- */

    /**
     * 첨부파일 목록
     */
    public List<AtchFileDtlDto> getAtchFileList() {
        if (this.getAtchFileNo() == null || this.getAtchFileInfo() == null) return null;
        return this.getAtchFileInfo()
                   .getAtchFileList();
    }
}
