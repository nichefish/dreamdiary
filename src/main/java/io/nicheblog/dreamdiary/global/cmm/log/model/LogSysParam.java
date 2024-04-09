package io.nicheblog.dreamdiary.global.cmm.log.model;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import lombok.*;

/**
 * LogSysParam
 * <pre>
 *  시스템 로그 파라미터 Dto
 * </pre>
 * TODO: 점진적으로 항목 추가쓰
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogSysParam
        extends BaseLogParam {

    /** 로그 고유 ID */
    private Integer logSysNo;
    /** 작업자 ID = 시스템 계정 */
    @Builder.Default
    private String userId = Constant.SYSTEM_ACNT;

    /* ----- */

    /**
     * 생성자
     */
    public LogSysParam(final Boolean rslt) {
        super(rslt);
    }
    public LogSysParam(final Boolean rslt, final String rsltMsg) {
        super(rslt, rsltMsg);
    }
    public LogSysParam(final Boolean rslt, final String rsltMsg, final ActvtyCtgr actvtyCtgr) {
        super(rslt, rsltMsg, actvtyCtgr);
    }
}
