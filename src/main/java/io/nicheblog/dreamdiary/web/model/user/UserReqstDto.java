package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * UserDto
 * <pre>
 *  사용자(계정) 정보 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserReqstDto
        extends BaseAtchDto {

    /** 사용자 고유 번호 (PK) */
    private Integer userNo;
    /** 아이디 */
    private String userId;
    /** 비밀번호 */
    private String password;
    /** 표시이름 */
    private String nickNm;

    /** 연락처 */
    private String cttpc;
    /** E-mail ID */
    private String emailId;
    /** E-mail 뒷부분 */
    private String emailDomain;

    /** 접속 IP 사용 여부 (Y/N) */
    @Builder.Default
    private String useAcsIpYn = "N";
    /** 접속 IP 정보 (String) */
    private String acsIpListStr;
    /** 접속 IP 목록 */
    private List<UserAcsIpDto> acsIpList;

    /** 계정 설명 (관리자용) */
    private String cn;

    /** 사용자 정보 (위임) */
    private UserProflDto userProfl;

    /* ----- */

    /** 접속IP 사용 여부 채크 */
    public Boolean getIsAcsIpY() {
        return "Y".equals(this.useAcsIpYn);
    }
}
