package io.nicheblog.dreamdiary.global.intrfc.model.param;

import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * BaseParam
 * <pre>
 *  (공통/상속) 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class BaseParam {

    /** 작업 카테고리 코드 */
    private String actvtyCtgrCd;
    /** 작업 카테고리 */
    private ActvtyCtgr actvtyCtgr;
    /** 액션구분 코드 */
    private String actionTyCd;

    /** 수정/상세에서 목록 화면으로 돌아옴 구분 코드 ( Y/N ) */
    private String isBackToList;

    // TODO: UTM 파라미터...

    /* ----- */

    public Boolean isBackToList() {
        return "Y".equals(this.isBackToList);
    }

    /**
     * 액션유형 체크 함수
     */
    public Boolean isAction(final String actionTyCd) {
        if (StringUtils.isEmpty(actionTyCd)) return false;
        return actionTyCd.equals(this.actionTyCd);
    }

    /* ----- */

    /** Getter :: 작업 카테고리 조회 */
    public ActvtyCtgr getActvtyCtgr() {
        if (this.actvtyCtgr != null) return this.actvtyCtgr;
        if (StringUtils.isEmpty(this.actvtyCtgrCd)) return ActvtyCtgr.DEFAULT;
        return ActvtyCtgr.valueOf(this.actvtyCtgrCd.toUpperCase());
    }

}
