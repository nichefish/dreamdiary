package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEmbed;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEntity;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserAcsIpMapstruct;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.OrderBy;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserAcsIpInfo
 * <pre>
 *  사용자user에서 계정 상태 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAcsIpCmpstn {

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

    /* ----- */

    /** 생성자 */
    public UserAcsIpCmpstn(UserAcsIpEmbed embed) {
        this();
        this.useAcsIpYn = embed.getUseAcsIpYn();
        if (!CollectionUtils.isEmpty(embed.getAcsIpList())) {
            this.useAcsIp = "Y".equals(embed.getUseAcsIpYn());
            this.acsIpList = embed.getAcsIpList().stream()
                    .map(acsIp -> {
                        try {
                            return UserAcsIpMapstruct.INSTANCE.toDto(acsIp);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }
}