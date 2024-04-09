package io.nicheblog.dreamdiary.global.cmm.log.model;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.PostLoad;
import javax.servlet.http.HttpServletRequest;

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
public class LogActvtyParam extends BaseLogParam {

    @PostLoad
    private void onLoad() {

    }

    /** 로그 고유 ID */
    private Integer logActvtysNo;
    /** 사용자 ID */
    private String userId;

    /** 액션 구분 코드 */
    private String actionTyCd;

    /** URL */
    private String url;
    /** 메소드 */
    private String mthd;
    /** 파라미터 */
    private String param;
    /** 리퍼터 */
    private String referer;
    /** IP 주소 */
    private String ipAddr;

    /* ----- */

    /**
     * 생성자
     * 객체 생성시 request 관련 인자들 자동 세팅 (가능하면)
     */
    public LogActvtyParam() {
        this.setRequestAttr();
    }
    public LogActvtyParam(final Boolean rslt) {
        super(rslt);
        this.setRequestAttr();
    }
    public LogActvtyParam(final Boolean rslt, final String rsltMsg) {
        super(rslt, rsltMsg);
        this.setRequestAttr();
    }
    public LogActvtyParam(final Boolean rslt, final String rsltMsg, final ActvtyCtgr actvtyCtgr) {
        super(rslt, rsltMsg, actvtyCtgr);
        this.setRequestAttr();
    }
    public LogActvtyParam(final String userId, final Boolean rslt, final String rsltMsg, final ActvtyCtgr actvtyCtgr) {
        super(rslt, rsltMsg, actvtyCtgr);
        this.userId = userId;
        this.setRequestAttr();
    }

    /** request 관련 내용들 */
    public void setRequestAttr() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // request 관련 인자들 미리 세팅 (가능하다면)
        if (attr == null) return;
        HttpServletRequest request = attr.getRequest();

        this.url = request.getServletPath();         // 작업 url
        this.mthd = request.getMethod();           // 접근 메소드
        this.param = request.getQueryString();       // 작업 파라미터
        this.referer = request.getHeader(Constant.REFERER);      // 리퍼러
        this.ipAddr = AuthUtils.getAcsIpAddr();       // 작업 IP
    }

    /**
     * 액션유형 체크 함수
     */
    public Boolean isAction(final String actionTyCd) {
        if (StringUtils.isEmpty(actionTyCd)) return false;
        return actionTyCd.equals(this.actionTyCd);
    }

}
