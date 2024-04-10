package io.nicheblog.dreamdiary.web.model.user.reqst;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.web.model.user.UserAcsIpCmpstn;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
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
        extends UserDto {

    /** 계정 설명 (관리자용) */
    private String cn;

    /* ----- */

    /** 접속 IP 정보 (위임) */
    private UserAcsIpCmpstn acsIpInfo;
    /** 사용자 정보 (위임) */
    private UserProflDto userProfl;
}
