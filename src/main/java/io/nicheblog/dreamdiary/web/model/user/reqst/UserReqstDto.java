package io.nicheblog.dreamdiary.web.model.user.reqst;

import io.nicheblog.dreamdiary.web.model.user.UserAcsIpDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

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
        extends UserDto {

    /** 접속IP 사용 여부 체크 */
    @Builder.Default
    private Boolean useAcsIp = false;
    /** 접속 IP 사용 여부 */
    @Builder.Default
    private String useAcsIpYn = "N";
    /** 접속 IP 정보 */
    private String acsIpListStr;
    /** 접속 IP 정보 */
    private List<UserAcsIpDto> acsIpList;

    /** 계정 설명 (관리자용) */
    private String cn;

    /* ----- */

    /** 이메일 반환 (override) */
    public String getEmail() {
        if (!StringUtils.isEmpty(this.email)) return this.email;
        return this.emailId + "@" + this.emailDomain;
    }
}
