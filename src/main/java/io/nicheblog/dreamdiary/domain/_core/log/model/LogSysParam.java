package io.nicheblog.dreamdiary.domain._core.log.model;

import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.Constant;
import lombok.*;

/**
 * LogSysParam
 * <pre>
 *  시스템 로그 파라미터.
 * </pre>
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
     * 생성자.
     * @param rslt - 처리 결과 상태를 나타내는 Boolean 값
     */
    public LogSysParam(final Boolean rslt) {
        super(rslt);
    }

    /**
     * 생성자.
     * @param rslt - 처리 결과 상태를 나타내는 Boolean 값
     * @param rsltMsg 처리 결과 메시지
     */
    public LogSysParam(final Boolean rslt, final String rsltMsg) {
        super(rslt, rsltMsg);
    }

    /**
     * 생성자.
     * @param rslt 처리 결과 상태를 나타내는 Boolean 값
     * @param rsltMsg 처리 결과 메시지
     * @param actvtyCtgr 활동 카테고리를 나타내는 `ActvtyCtgr` 객체
     */
    public LogSysParam(final Boolean rslt, final String rsltMsg, final ActvtyCtgr actvtyCtgr) {
        super(rslt, rsltMsg, actvtyCtgr);
    }
}
