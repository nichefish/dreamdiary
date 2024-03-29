package io.nicheblog.dreamdiary.web.model.board;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseEnhcPostDto;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
        extends BaseEnhcPostDto {

    /** 컨텐츠 타입 :: 화면단 + dto 레벨에서는 boardCd, entity 단에서는 contentType */
    @JsonProperty("contentType")
    private String boardCd;

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

    /**
     * 게시물 조치자 목록 추가
     */
    // public void addPostManagtr(final BoardPostManagtrDto managtr) {
    //     if (this.managtrList == null) this.managtrList = new ArrayList<>();
    //     managtrList.add(managtr);
    // }

    /**
     * 처리(조치)자 여부
     */
    //public Boolean getIsManagtr() {
    //    return (AuthUtils.isRegstr(this.managtrId));
    //}

    /* ----- 댓글 모듈 ----- */

    /** 댓글 목록 */
    private List<CommentDto> commentList;
    /** 댓글 갯수 */
    @Builder.Default
    private Integer commentCnt = 0;
    /** 댓글 존재 여부 */
    @Builder.Default
    private Boolean hasComment = false;
}
