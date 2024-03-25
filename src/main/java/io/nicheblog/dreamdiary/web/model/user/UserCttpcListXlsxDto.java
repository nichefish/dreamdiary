package io.nicheblog.dreamdiary.web.model.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserCttpcListXlsxDto
 * <pre>
 *  사용자 연락처 목록 엑셀 다운로드 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserCttpcListXlsxDto {

    /**
     * 이름
     */
    private String userNm;
    /**
     * 직급이름
     */
    private String jobTitleNm;
    /**
     * 소속(팀)
     */
    private String teamNm;
    /**
     * 연락처(전화번호)
     */
    private String cttpc;
    /**
     * E-mail
     */
    private String email;
}
