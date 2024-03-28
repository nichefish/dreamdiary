package io.nicheblog.dreamdiary.web.model.board;

import io.nicheblog.dreamdiary.global.auth.model.AuditorDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * BoardPostDto
 * <pre>
 *  게시판 게시물 Dto
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

    /**
     * 게시물 조치자 목록
     *//*

    private List<BoardPostManagtrDto> managtrList;
    */

    /**
     * (수정시) 조치일자 변경하지 않음 변수
     */
    private String managtDtUpdtYn;

/**
     * 열람자 목록
     *//*

    private List<BoardPostViewerDto> viewerList;
    */
/**
     * 게시물 태그 목록
     *//*

    private List<BoardPostTagDto> tagList;
    */
/**
     * 태그 정보 (String)
     *//*

    @JsonIgnore
    private String tagListStr;

    */
/**
     * 노션 페이지 참조 ID :: UUID
     *//*

    // private String notionPageId;

    */
/**
     * 파일시스템 참조 목록
     *//*

    // private List<FlsysRefDto> flsysRefList;

    */
/* ----- *//*


    */
/**
     * 내부 값들 합쳐서 풀 타이틀 반환
     *//*

    public String getFullTitle() {
        String title = this.title;
        if (StringUtils.isNotEmpty(this.ctgrNm)) title = "[" + this.ctgrNm + "] " + title;
        return title;
    }


    */

/**
     * 게시물 조회자 목록 추가
     *//*

    public void addPostViewer(final BoardPostViewerDto viewer) {
        if (this.viewerList == null) this.viewerList = new ArrayList<>();
        viewerList.add(viewer);
    }

    */
/**
     * 게시물 조회자 목록 추가
     *//*

    public void addPostManagtr(final BoardPostManagtrDto managtr) {
        if (this.managtrList == null) this.managtrList = new ArrayList<>();
        managtrList.add(managtr);
    }

    */
/**
     * 처리(조치)자 여부
     *//*

    public Boolean getIsManagtr() {
        return (AuthUtils.isRegstr(this.managtrId));
    }

    */
/**
     * 태그 목록 (dto) -> 태그 목록 (entity)
     *//*

    public List<BoardPostTagEntity> getTagEntityList() throws Exception {
        if (CollectionUtils.isEmpty(this.tagList)) return null;
        List<BoardPostTagEntity> tagEntityList = new ArrayList<>();
        for (BoardPostTagDto tag : this.tagList) {
            BoardPostTagEntity entity = BoardPostTagMapstruct.INSTANCE.toEntity(tag);
            tagEntityList.add(entity);
        }
        return tagEntityList;
    }

    */
/**
     * 태그 문자열 목록 반환
     *//*

    public List<String> getTagStrList() {
        if (CollectionUtils.isEmpty(this.tagList)) return null;
        List<String> tagStrList = new ArrayList<>();
        for (BoardPostTagDto tag : this.tagList) {
            if (StringUtils.isEmpty(tag.getBoardTag())) continue;
            tagStrList.add(tag.getBoardTag());
        }
        return tagStrList;
    }
*/
}
