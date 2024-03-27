package io.nicheblog.dreamdiary.global.cmm.log.model;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
public class LogSysParam {

    /**
     * 작업 카테고리 코드
     */
    private ActvtyCtgr actvtyCtgr;
    /**
     * 성공여부
     */
    private Boolean isSuccess;
    /**
     * 결과 메세지
     */
    private String resultMsg;


    /**
     * 로그 고유 ID
     */
    private Integer logSysNo;
    /**
     * 작업자 ID
     */
    private String logUserId = Constant.SYSTEM_ACNT;
    /**
     * 작업자 이름
     */
    private String logUserNm;
    /**
     * 작업일시
     */
    private String logDt;

    /**
     * 익셉션 이름
     */
    private String exceptionNm;
    /**
     * 익셉션 메세지
     */
    private String exceptionMsg;

    /* ----- */

    /**
     * 생성자
     */
    public LogSysParam(
            final boolean isSuccess,
            final String resultMsg
    ) {
        this.isSuccess = isSuccess;
        this.resultMsg = resultMsg;
    }

    /**
     * 결과 세팅 함수
     */
    public LogSysParam setResult(
            final boolean isSuccess,
            final String resultMsg,
            final ActvtyCtgr actvtyCtgr
    ) {
        this.setResult(isSuccess, resultMsg);
        this.actvtyCtgr = actvtyCtgr;
        return this;
    }

    public LogSysParam setResult(
            final boolean isSuccess,
            final String resultMsg
    ) {
        this.isSuccess = isSuccess;
        this.resultMsg = resultMsg;
        return this;
    }

    /**
     * Exception 정보 세팅 함수
     */
    public void setExceptionInfo(
            final String exceptionNm,
            final String exceptionMsg
    ) {
        this.exceptionNm = exceptionNm;
        this.exceptionMsg = exceptionMsg;
    }
}
