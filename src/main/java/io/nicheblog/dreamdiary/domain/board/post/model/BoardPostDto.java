package io.nicheblog.dreamdiary.domain.board.post.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.nicheblog.dreamdiary.global._common._clsf.comment.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global._common._clsf.comment.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.global._common._clsf.managt.model.cmpstn.ManagtCmpstn;
import io.nicheblog.dreamdiary.global._common._clsf.managt.model.cmpstn.ManagtCmpstnModule;
import io.nicheblog.dreamdiary.global._common._clsf.tag.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global._common._clsf.tag.model.cmpstn.TagCmpstnModule;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.model.cmpstn.ViewerCmpstn;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.model.cmpstn.ViewerCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

/**
 * BoardPostDto
 * <pre>
 *  게시판 게시물 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BoardPostDto
        extends BasePostDto
        implements Identifiable<BaseClsfKey>, CommentCmpstnModule, TagCmpstnModule, ManagtCmpstnModule, ViewerCmpstnModule {

    /** 컨텐츠 타입 :: 화면단 + dto 레벨에서는 boardCd, entity 단에서는 contentType */
    @JsonProperty("contentType")
    @Size(max = 50)
    protected String boardCd;

    /* ----- */

    /**
     * 게시판 게시물 상세 (DTL) Dto.
     */
    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class DTL
            extends BoardPostDto {
        /** 노션 페이지 참조 ID :: UUID */
        // private String notionPageId;

        /** 파일시스템 참조 목록 */
        // private List<FlsysRefDto> flsysRefList;
    }

    /**
     * 게시판 게시물 목록 조회 (LIST) Dto.
     */
    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class LIST
            extends BoardPostDto {
        //
    }

    /* ----- */

    @Override
    public BaseClsfKey getKey() {
        return new BaseClsfKey(this.postNo, this.boardCd);
    }

    /** 위임 :: 댓글 정보 모듈 */
    public CommentCmpstn comment;
    /** 위임 :: 태그 정보 모듈 */
    public TagCmpstn tag;
    /** 위임 :: 조치 정보 모듈 */
    public ManagtCmpstn managt;
    /** 위임 :: 열람 정보 모듈 */
    public ViewerCmpstn viewer;
}
