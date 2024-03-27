package io.nicheblog.dreamdiary.web.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserInfoAcsIpDto
 * <pre>
 *  사용자 접속 IP Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class UserAcsIpDto {

    /**
     * 사용자 접속 IP 고유 ID (PK)
     */
    private Integer userAcsIpNo;
    /**
     * 사용자 고유 ID (FK)
     */
    private Integer userNo;
    /**
     * 접속IP
     */
    private String acsIp;
}
