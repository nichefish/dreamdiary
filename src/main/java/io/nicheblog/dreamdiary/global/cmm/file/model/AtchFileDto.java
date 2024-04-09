package io.nicheblog.dreamdiary.global.cmm.file.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * AtchFileDto
 * <pre>
 *  첨부파일 Dto
 *  ※첨부파일(atch_file) = 여러 첨부파일을 하나의 단위로 묶어놓은 객체. 첨부파일 상세(atch_file_dtl)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class AtchFileDto
        extends BaseCrudDto {

    /** 첨부파일 번호 (PK) */
    private Integer atchFileNo;

    /** 첨부파일 목록 */
    List<AtchFileDtlDto> atchFileList;
}
