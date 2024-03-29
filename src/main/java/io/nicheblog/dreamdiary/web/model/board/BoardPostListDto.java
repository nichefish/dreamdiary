package io.nicheblog.dreamdiary.web.model.board;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embedded;

/**
 * BoardPostListDto
 * <pre>
 *  일반게시판 게시물 목록 조회 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostListDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BoardPostListDto
        extends BasePostListDto {

    /** 컨텐츠 타입 :: 화면단 + dto 레벨에서는 boardCd, entity 단에서는 contentType */
    @JsonProperty("contentType")
    private String boardCd;

    /** 글분류 분류코드 */
    private String ctgrClCd;

    /** 노션 페이지 참조 ID :: UUID */
    // private String notionPageId;

    /* ----- */

    /** TODO : 열람자 목록 */
    /** 게시물 태그 목록 */
//    private List<BoardPostTagDto> tagList;

/**
     * 태그 목록 문자열 반환
     *//*

    public String getTagListStr() {
        if (CollectionUtils.isEmpty(this.tagList)) return null;
        StringBuilder a = new StringBuilder();
        for (BoardPostTagDto tag : this.tagList) {
            if (a.length() > 0) a.append(",");
            a.append(tag.getBoardTag());
        }
        return a.toString();
    }

    /** 게시물 태그 목록 */
    // private List<BoardPostTagDto> tagList;

    /** 태그 정보 (String) */
    // @JsonIgnore
    // private String tagListStr;

    /**
     * 태그 목록 (dto) -> 태그 목록 (entity)
     */
    // public List<BoardPostTagEntity> getTagEntityList() throws Exception {
    //     if (CollectionUtils.isEmpty(this.tagList)) return null;
    //     List<BoardPostTagEntity> tagEntityList = new ArrayList<>();
    //     for (BoardPostTagDto tag : this.tagList) {
    //         BoardPostTagEntity entity = BoardPostTagMapstruct.INSTANCE.toEntity(tag);
    //         tagEntityList.add(entity);
    //     }
    //     return tagEntityList;
    // }

    /**
     * 태그 문자열 목록 반환
     */
    // public List<String> getTagStrList() {
    //     if (CollectionUtils.isEmpty(this.tagList)) return null;
    //     List<String> tagStrList = new ArrayList<>();
    //     for (BoardPostTagDto tag : this.tagList) {
    //         if (StringUtils.isEmpty(tag.getBoardTag())) continue;
    //         tagStrList.add(tag.getBoardTag());
    //     }
    //     return tagStrList;
    // }

    /**
     * 태그 목록 문자열 반환
     */
    // @JsonIgnore
    // public String getTagListStr() {
    //     if (CollectionUtils.isEmpty(this.getTagList())) return null;
    //     StringBuilder a = new StringBuilder();
    //     for (BoardPostTagDto tag : this.getTagList()) {
    //         if (a.length() > 0) a.append(",");
    //         a.append(tag.getBoardTag());
    //     }
    //     return a.toString();
    // }


    /** 열람자 목록 */
    // private List<BoardPostViewerDto> viewerList;
/**
     * 태그 문자열 목록 반환
     *//*

    public List<String> getTagStrList() throws Exception {
        if (CollectionUtils.isEmpty(this.tagList)) return null;
        List<String> tagStrList = new ArrayList<>();
        for (BoardPostTagDto tag : this.tagList) {
            tagStrList.add(tag.getBoardTag());
        }
        return tagStrList;
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtEmbed managt;
}

