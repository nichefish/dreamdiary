package io.nicheblog.dreamdiary.web.model.cmm.flsys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FlsysRefDto
 * <pre>
 *  파일시스템 참조 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FlsysRefDto {

    /** 사용자 고유 ID (PK) */
    private Integer flsysRefNo;
    /** 글 번호 */
    private String postNo;
    /** 게시판분류코드 */
    private String boardCd;
    /** 파일 경로 */
    private String filePath;
}
