package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstnModule;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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
        extends BaseAtchDto
        implements Identifiable<Integer>, StateCmpstnModule {

    /** 팝업 코드 */
    @Size(max = 50)
    private String popupCd;

    /** 팝업 이름 */
    private String popupNm;

    /** 가로 */
    @Positive
    private Integer width;

    /** 세로 */
    @Positive
    private Integer height;

    /** 게시시작일시 */
    private String popupStartDt;
    /** 게시종료일시 */
    private String popupEndDt;

    /* ----- */

    /** 상태 관리 모듈 (위임) */
    public StateCmpstn state;

    @Override
    public Integer getKey() {
        return null;
    }
}
