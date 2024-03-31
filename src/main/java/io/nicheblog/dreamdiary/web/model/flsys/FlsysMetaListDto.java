package io.nicheblog.dreamdiary.web.model.flsys;

import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embedded;

/**
 * FlsysMetaDto
 * <pre>
 *  파일시스템 메타 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostListDto
 * @implements CommentCmpstnModule, TagCmpstnModule, ManagtCmpstnModule, ViewerCmpstnModule
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(of = {"filePath"}, callSuper = false)
public class FlsysMetaListDto
        extends BasePostListDto
        implements CommentCmpstnModule, TagCmpstnModule, ManagtCmpstnModule, ViewerCmpstnModule {

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
    public FlsysMetaListDto(final String filePath) {
        this.filePath = filePath;
    }

    public String getBoardCd() {
        return "flsysMeta";
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;

    /** 태그 정보 모듈 (위임) */
    @Embedded
    public TagCmpstn tag;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtCmpstn managt;

    /** 열람자 정보 모듈 (위임) */
    @Embedded
    public ViewerCmpstn viewer;
}
