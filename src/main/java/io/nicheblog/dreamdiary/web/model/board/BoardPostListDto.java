package io.nicheblog.dreamdiary.web.model.board;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.model.AuditorDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * BoardPostListDto
 * <pre>
 *  게시판 게시물 목록 조회 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostListDto
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BoardPostListDto
        extends BasePostListDto {

    /**
     * 글분류 분류코드
     */
    private String ctgrClCd;

    /**
     * 글분류 코드
     */
    private String ctgrCd;

    /**
     * 글분류 코드 이름
     */
    private String ctgrNm;

    /**
     * 수정권한
     */
    @Builder.Default
    private String mdfable = Constant.MDFABLE_REGSTR;

    /**
     * 노션 페이지 참조 ID :: UUID
     */
    /*
    private String notionPageId;

    */
/**
     * 댓글 갯수
     *//*

    private Integer commentCnt = 0;

    */
/**
     * 조치자(작업자)ID
     */
    private String managtrId;

    /**
     * 조치자(작업자)이름
     */
    private String managtrNm;

    /**
     * 조치일시
     */
    private String managtDt;

    /**
     * 조치자 정보
     */
    private AuditorDto managtrInfo;

    /** TODO : 열람자 목록 *//*

    */
/**
     * 게시물 태그 목록
     *//*

    private List<BoardPostTagDto> tagList;

    */
/* ----- *//*


    */
/**
     * 처리(조치)자 여부
     *//*

    public Boolean getIsManagtr() {
        return (AuthUtils.isRegstr(this.managtrId));
    }

    */
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

    */
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
    */
}

