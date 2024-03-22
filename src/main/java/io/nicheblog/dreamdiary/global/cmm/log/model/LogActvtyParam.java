package io.nicheblog.dreamdiary.global.cmm.log.model;

import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * LogActvtyParam
 * <pre>
 *  활동 로그 파라미터 Dto
 *  (화면에서 넘어오는 인자 정리 위한 클래스)
 * </pre>
 * TODO: 점진적으로 항목 추가쓰
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class LogActvtyParam {

    /**
     * 작업 카테고리
     */
    private ActvtyCtgr actvtyCtgr;
    /**
     * 액션 구분 코드
     */
    private String actionTyCd;
    /**
     * 성공여부
     */
    private Boolean isSuccess;
    /**
     * 결과 메세지
     */
    private String resultMsg;

    /**
     * 내용
     */
    private String cn;
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
    public LogActvtyParam(final Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public LogActvtyParam(
            final Boolean isSuccess,
            final String resultMsg
    ) {
        this(isSuccess);
        this.resultMsg = resultMsg;
    }

    public LogActvtyParam(
            final Boolean isSuccess,
            final String resultMsg,
            final ActvtyCtgr actvtyCtgr
    ) {
        this(isSuccess, resultMsg);
        this.actvtyCtgr = actvtyCtgr;
    }

    /**
     * 결과 세팅 함수
     */
    public LogActvtyParam setResult(
            final boolean isSuccess,
            final String resultMsg,
            final ActvtyCtgr actvtyCtgr
    ) {
        this.setResult(isSuccess, resultMsg);
        this.actvtyCtgr = actvtyCtgr;
        return this;
    }

    public LogActvtyParam setResult(
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

    /**
     * 값 없음 반환 함수
     */
    public Boolean hasNoAttr(final String attrNm) {
        return !this.hasAttr(attrNm);
    }

    public Boolean hasAttr(final String attrNm) {
        switch (attrNm) {
            case "actvtyCtgr":
                return StringUtils.isNotEmpty(this.actvtyCtgr.name());
        }
        return false;
    }

    /**
     * 액션유형 체크 함수
     */
    public Boolean isAction(final String actionTyCd) {
        if (StringUtils.isEmpty(actionTyCd)) return false;
        return actionTyCd.equals(this.actionTyCd);
    }

}
