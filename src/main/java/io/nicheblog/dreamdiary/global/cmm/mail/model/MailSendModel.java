package io.nicheblog.dreamdiary.global.cmm.mail.model;

import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MailSendModel
 * TODO: 봐야한다.
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class MailSendModel {
    public String subject; // 제목
    public String tpl;  // 본문 템플릿
    public String code;  // 템플릿 코드
    public MailAddress sender = null; // 발신자 (홍길동<test@gmail.com>)
    public List<MailAddress> recipientList; // 수신자 Array(홍길동<test@gmail.com>)
    public List<AtchFileDtlDto> atchFileList; // 첨부파일

    public Map<String, Object> data = new HashMap<String, Object>();

    public MailSendModel(final String tpl) {
        this.tpl = tpl;
        this.recipientList = new ArrayList<MailAddress>();
        this.atchFileList = new ArrayList<AtchFileDtlDto>();
    }

    public MailSendModel(
            final String tpl,
            final MailAddress sender
    ) {
        this.tpl = tpl;
        this.recipientList = new ArrayList<MailAddress>();
        this.atchFileList = new ArrayList<AtchFileDtlDto>();
        this.sender = sender;
    }
}
