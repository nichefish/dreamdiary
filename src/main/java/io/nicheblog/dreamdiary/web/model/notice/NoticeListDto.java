package io.nicheblog.dreamdiary.web.model.notice;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * NoticeListDto
 * <pre>
 *  공지사항 목록 조회 Dto
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
public class NoticeListDto
        extends BasePostListDto {

    /**
     * 글분류 코드
     */
    private String ctgrCd;
    /**
     * 글분류 코드 이름
     */
    private String ctgrNm;
    /**
     * 로그인 페이지 노출여부
     */
    @Builder.Default
    private String popupYn = "N";

    /**
     * 수정권한
     */
    @Builder.Default
    private String mdfable = Constant.MDFABLE_REGSTR;

    /* ----- */

    /**
     * 게시판분류코드 Getter (override)
     */
    public String getBoardCd() {
        return "notice";
    }

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

}
