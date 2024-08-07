package io.nicheblog.dreamdiary.web.model.log;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;

/**
 * LogActvtyDto
 * <pre>
 *  활동 로그 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class LogActvtyDto
        extends BaseCrudDto
        implements Identifiable<Integer> {

    /** 로그 고유 번호 (PK) */
    private Integer logActvtyNo;
    /** 작업자 ID */
    private String userId;
    /** 작업자 이름 */
    private String logUserNm;
    /** 권한코드 */
    private String authCd;
    /** 권한명 */
    private String authNm;
    /** 작업일시 */
    private String logDt;

    /** 작업 구분 코드 (ex. 게시판, 공지사항, ...) (기능/모듈 단위) */
    private String actvtyCtgrCd;
    /** 작업 구분 코드 (ex. 게시판, 공지사항, ...) (기능/모듈 단위) */
    private String actvtyCtgrNm;
    /** 작업 유형 코드 (조회, 검색, 제출, 처리...) */
    private String actionTyCd;
    /** 작업 유형 코드 (조회, 검색, 제출, 처리...) */
    private String actionTyNm;

    /** 작업 URL */
    private String url;
    /** 작업 URL 이름 */
    private String urlNm;
    /** 작업 파라미터 */
    private String param;
    /** 작업 파라미터 맵 */
    private HashMap<String, String> paramMap;

    /** 메소드 */
    private String mthd;
    /** 작업 내용 */
    private String cn;

    /** 작업자 IP */
    private String ipAddr;
    /** 리퍼러 */
    private String referer;

    /** 작업 결과 */
    private String rslt;
    /** 작업 결과 메세지 */
    private String rsltMsg;
    /** 익셉션 이름 */
    private String exceptionNm;
    /** 익셉션 메세지 */
    private String exceptionMsg;

    /* ----- */

    public Boolean isSuccess() {
        return "true".equals(this.rslt);
    }

    /**
     * 작업자 여부
     */
    public Boolean getIsJobUser() {
        return (AuthUtils.isRegstr(this.userId));
    }

    /**
     * 작업 파라미터 = "null"은 표시하지 않음
     */
    public String getParam() {
        if ("null".equals(this.param)) return "-";
        return this.param;
    }

    @Override
    public Integer getKey() {
        return this.logActvtyNo;
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class DTL extends LogActvtyDto {
        //
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class LIST extends LogActvtyDto {
        //
    }
}
