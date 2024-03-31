package io.nicheblog.dreamdiary.web.model.flsys;

import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embedded;
import java.io.Serializable;

/**
 * FlsysMetaDto
 * <pre>
 *  파일시스템 메타 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(of = {"filePath"}, callSuper = false)
public class FlsysMetaDto
        extends BasePostDto
        implements Serializable {

    private Integer postNo;
    private String boardCd;

    /**
     * 파일절대경로
     */
    private String filePath;
    /**
     * 상위파일절대경로
     */
    private String upperFilePath;

    /* ----- */

    /**
     * 생성자
     */
    public FlsysMetaDto(final String filePath) {
        this.filePath = filePath;
    }

    public String getBoardCd() {
        return "flsysMeta";
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;
}
