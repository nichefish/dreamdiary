package io.nicheblog.dreamdiary.global.cmm.mail.model;

import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * MailSendParam
 * <pre>
 *  메일 발송 파라미터
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MailSendParam {
    
    /** 제목 */
    public String subject;

    /** 본문 템플릿 */
    public String tmplat;

    /** 발신자 (홍길동<test@gmail.com>) */
    public MailAddress sender;

    /** 수신자 Array(홍길동<test@gmail.com>) */
    public List<MailAddress> recipientList;

    /** 첨부파일 목록 */
    public List<AtchFileDtlDto> atchFileList;

    /** 데이터:: freemarker 템플릿 처리 */
    public Map<String, Object> dataMap;

    /* ----- */

    /** 생성자 */
    public MailSendParam(final String tmplat) {
        this.tmplat = tmplat;
    }
    public MailSendParam(final String tmplat, final MailAddress sender) {
        this(tmplat);
        this.sender = sender;
    }
}
