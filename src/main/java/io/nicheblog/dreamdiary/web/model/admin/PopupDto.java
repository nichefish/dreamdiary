package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * PopupDto
 * <pre>
 *  팝업 정보 관리 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PopupDto
        extends BaseAtchDto {

    /**
     * 팝업 코드
     */
    private String popupCd;
    /**
     * 팝업 이름
     */
    private String popupNm;
    /**
     * 가로
     */
    private Integer width;
    /**
     * 세로
     */
    private Integer height;

    /**
     * 게시시작일시
     */
    private String popupStartDt;
    /**
     * 게시종료일시
     */
    private String popupEndDt;
    /**
     * 사용여부
     */
    private String useYn;
}
