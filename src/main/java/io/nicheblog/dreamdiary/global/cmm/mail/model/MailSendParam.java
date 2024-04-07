package io.nicheblog.dreamdiary.global.cmm.mail.model;

import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailSendParam {
    
    /** 제목 */
    public String subject;

    /** 본문 템플릿 */
    public String tmplat;

    /** 발신자 (홍길동<test@gmail.com>) */
    public MailAddress sender = null;

    /** 수신자 Array(홍길동<test@gmail.com>) */
    @Builder.Default
    public List<MailAddress> recipientList = new ArrayList<>();

    /** 첨부파일 목록 */
    @Builder.Default
    public List<AtchFileDtlDto> atchFileList = new ArrayList<>();

    /** 데이터:: freemarker 템플릿 처리 */
    public Map<String, Object> dataMap = new HashMap<>();

    /* ----- */

    /** 생성자 */
    public MailSendParam(final String tmplat) {
        this.tmplat = tmplat;
    }
    public MailSendParam(final String tmplat, final MailAddress sender) {
        this.tmplat = tmplat;
        this.sender = sender;
    }
}
