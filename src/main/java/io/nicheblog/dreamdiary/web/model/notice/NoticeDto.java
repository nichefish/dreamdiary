package io.nicheblog.dreamdiary.web.model.notice;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * NoticeDto
 * <pre>
 *  공지사항 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class NoticeDto
        extends BasePostDto {

    /**
     * 중요여부
     */
    @Builder.Default
    private String imprtcYn = "N";
    /**
     * 글분류 코드
     */
    private String ctgrCd;
    /**
     * 글분류 코드 이름
     */
    private String ctgrNm;
    /**
     * 상단고정여부
     */
    @Builder.Default
    private String fxdYn = "N";
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

    /**
     * 파일시스템 참조 목록
     */
    // private List<FlsysRefDto> flsysRefList;

    /* ----- */

    /**
     * 게시판분류코드 Getter (override)
     */
    public String getBoardCd() {
        return "notice";
    }

    /**
     * 내부 값들 합쳐서 풀 타이틀 반환
     */
    public String getFullTitle() {
        String title = this.postSj;
        if (StringUtils.isNotEmpty(this.ctgrNm)) title = "[" + this.ctgrNm + "] " + title;
        if ("Y".equals(this.imprtcYn)) title = "[중요] " + title;
        return title;
    }
}
