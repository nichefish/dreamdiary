package io.nicheblog.dreamdiary.web.model.board;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embedded;

/**
 * BoardPostDto
 * <pre>
 *  일반게시판 게시물 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BoardPostDto
        extends BasePostDto {

    /** 컨텐츠 타입 :: 화면단 + dto 레벨에서는 boardCd, entity 단에서는 contentType */
    @JsonProperty("contentType")
    private String boardCd;

    /* ----- */

    /** 노션 페이지 참조 ID :: UUID */
    // private String notionPageId;

    /** 파일시스템 참조 목록 */
    // private List<FlsysRefDto> flsysRefList;

    /* ----- */

    /**
     * 내부 값들 합쳐서 풀 타이틀 반환
     */
    // public String getFullTitle() {
    //     String title = this.title;
    //     if (StringUtils.isNotEmpty(this.ctgrNm)) title = "[" + this.ctgrNm + "] " + title;
    //     return title;
    // }

    /**
     * 게시물 조회자 목록 추가
     */
    // public void addPostViewer(final BoardPostViewerDto viewer) {
    //     if (this.viewerList == null) this.viewerList = new ArrayList<>();
    //     viewerList.add(viewer);
    // }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtEmbed managt;
}
