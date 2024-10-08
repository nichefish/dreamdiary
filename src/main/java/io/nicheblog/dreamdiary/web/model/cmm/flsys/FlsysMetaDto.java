package io.nicheblog.dreamdiary.web.model.cmm.flsys;

import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Positive;

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
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule, ManagtCmpstnModule, ViewerCmpstnModule {

    /** 글번호 */
    @Positive
    private Integer postNo;

    /** 파일절대경로 */
    private String filePath;
    /** 상위파일절대경로 */
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

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /* ----- */

    public static class DTL extends FlsysMetaDto {
        // private List<FlsysRefDto> flsysRefList;
    }

    public static class LIST extends FlsysMetaDto {
        //
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
    /** 조치 정보 모듈 (위임) */
    public ManagtCmpstn managt;
    /** 열람자 정보 모듈 (위임) */
    public ViewerCmpstn viewer;
}
