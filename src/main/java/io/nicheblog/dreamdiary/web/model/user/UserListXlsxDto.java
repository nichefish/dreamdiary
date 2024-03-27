package io.nicheblog.dreamdiary.web.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserListXlsxDto
 * <pre>
 *  사용자(계정) 정보 목록 엑셀 다운로드 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class UserListXlsxDto {

    /**
     * 권한
     */
    private String authNm;
    /**
     * 아이디
     */
    private String userId;
    /**
     * 이름
     */
    private String nickNm;
    /**
     * 연락처(전화번호)
     */
    private String cttpc;
    /**
     * E-mail
     */
    private String email;
    /**
     * 잠금여부
     */
    private String lockedYn;
    /**
     * 접속 IP 정보 (String)
     */
    private String acsIpListStr;
    /**
     * 등록자 ID
     */
    protected String regstrId;
    /**
     * 최종접속일시
     */
    private String lstLgnDt;
}
