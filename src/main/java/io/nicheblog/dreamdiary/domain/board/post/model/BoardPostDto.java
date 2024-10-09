package io.nicheblog.dreamdiary.domain.board.post.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

/**
 * BoardPostDto
 * <pre>
 *  일반게시판 게시물 Dto.
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
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
     * 내부 값들 합쳐서 풀 타이틀 반환
     */
    // public String getFullTitle() {
    //     String title = this.title;
    //     if (StringUtils.isNotEmpty(this.ctgrNm)) title = "[" + this.ctgrNm + "] " + title;
    //     return title;
    // }

    /* ----- */

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
