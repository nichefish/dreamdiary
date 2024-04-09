package io.nicheblog.dreamdiary.global.cmm.log.model;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BaseLogParam
 * <pre>
 *  공통 로그 항목 분리 (활동 로그/시스템 로그)
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class BaseLogParam {

    /** 성공여부 */
    private Boolean rslt;
    /** 결과 메세지 */
    private String rsltMsg;
    /** 내용 */
    private String cn;

    /** 작업 카테고리 코드 */
    private ActvtyCtgr actvtyCtgr;

    /** 로그 고유 ID */
    private Integer logSysNo;
    /** 작업자 ID */
    private String userId = Constant.SYSTEM_ACNT;
    /** 작업자 이름 */
    private String userNm;
    /** 작업일시 */
    private String logDt;

    /** 익셉션 이름 */
    private String exceptionNm;
    /** 익셉션 메세지 */
    private String exceptionMsg;

    /* ----- */

    /**
     * 생성자
     */
    public BaseLogParam(final Boolean rslt) {
        this.rslt = rslt;
    }
    public BaseLogParam(final Boolean rslt, final String rsltMsg) {
        this(rslt);
        this.rsltMsg = rsltMsg;
    }
    public BaseLogParam(final Boolean rslt, final String rsltMsg, final ActvtyCtgr actvtyCtgr) {
        this(rslt, rsltMsg);
        this.actvtyCtgr = actvtyCtgr;
    }

    /**
     * 결과 세팅 함수
     */
    public BaseLogParam setResult(final boolean rslt, final String rsltMsg) {
        this.rslt = rslt;
        this.rsltMsg = rsltMsg;
        return this;
    }
    public BaseLogParam setResult(final boolean rslt, final String rsltMsg, final ActvtyCtgr actvtyCtgr) {
        this.setResult(rslt, rsltMsg);
        this.actvtyCtgr = actvtyCtgr;
        return this;
    }

    /**
     * Exception 정보 세팅 함수
     */
    public void setExceptionInfo(final Exception e) {
        this.exceptionNm = MessageUtils.getExceptionNm(e);
        this.exceptionMsg = MessageUtils.getExceptionMsg(e);
    }
}
