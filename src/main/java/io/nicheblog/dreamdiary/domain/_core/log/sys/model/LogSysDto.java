package io.nicheblog.dreamdiary.domain._core.log.sys.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * LogSysDto
 * <pre>
 *  시스템 로그 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class LogSysDto
        extends BaseCrudDto
        implements Identifiable<Integer> {

    /** 로그 고유 번호 */
    private Integer logSysNo;

    /** 작업자 ID */
    private String userId;

    /** 작업자 이름 */
    private String userNm;

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

    /** 작업 내용 */
    private String cn;

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

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class DTL extends LogSysDto {
        //
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class LIST extends LogSysDto {
        //
    }

    /* ----- */

    @Override
    public Integer getKey() {
        return this.logSysNo;
    }
}
